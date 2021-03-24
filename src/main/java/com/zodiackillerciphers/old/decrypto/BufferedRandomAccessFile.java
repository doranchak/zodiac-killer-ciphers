package com.zodiackillerciphers.old.decrypto;

import java.io.*;

/** This file obtained from:
    http://research.compaq.com/SRC/WebL/sources/webl.util.BufferedRandomAccessFile.java.html

    What is the license on this file?
**/

/** A <code>BufferedRandomAccessFile</code> is like a
    <code>RandomAccessFile</code>, but it uses a private
    buffer so that most operations do not require a disk
    access.<P>
    
    Note: The operations on this class are unmonitored.
    Also, the correct functioning of the <code>RandomAccessFile</code>
    methods that are not overridden here relies on the implementation
    of those methods in the superclass.
*/

public final class BufferedRandomAccessFile extends RandomAccessFile {
    static final int LogBuffSz = 16; // 64K buffer
    public static final int BuffSz = (1 << LogBuffSz);
    static final long BuffMask = ~(((long)BuffSz) - 1L);

    /* This implementation is based on the buffer implementation in
       Modula-3's "Rd", "Wr", "RdClass", and "WrClass" interfaces. */
    private boolean dirty;  // true iff unflushed bytes exist
    private boolean closed; // true iff the file is closed
    private long curr;      // current position in file
    private long lo, hi;    // bounds on characters in "buff"
    private byte[] buff;    // local buffer
    private long maxHi;     // this.lo + this.buff.length
    private boolean hitEOF; // buffer contains last file block?
    private long diskPos;   // disk position
    
    private static Object mu = new Object(); // protects the following fields
    private static byte[][] availBuffs = new byte[100][];
    private static int numAvailBuffs = 0;

    /* To describe the above fields, we introduce the following
       abstractions for the file "f":

          len(f)  the length of the file
         curr(f)  the current position in the file
            c(f)  the abstract contents of the file
         disk(f)  the contents of f's backing disk file
       closed(f)  true iff the file is closed

       "curr(f)" is an index in the closed interval [0, len(f)].
       "c(f)" is a character sequence of length "len(f)". "c(f)"
       and "disk(f)" may differ if "c(f)" contains unflushed
       writes not reflected in "disk(f)". The flush operation has
       the effect of making "disk(f)" identical to "c(f)".

       A file is said to be *valid* if the following conditions
       hold:

       V1. The "closed" and "curr" fields are correct:

           f.closed == closed(f)
           f.curr == curr(f)

       V2. The current position is either contained in the buffer,
           or just past the buffer:

           f.lo <= f.curr <= f.hi

       V3. Any (possibly) unflushed characters are stored
           in "f.buff":

           (forall i in [f.lo, f.curr):
             c(f)[i] == f.buff[i - f.lo])

       V4. For all characters not covered by V3, c(f) and
           disk(f) agree:

           (forall i in [f.lo, len(f)):
             i not in [f.lo, f.curr) => c(f)[i] == disk(f)[i])

       V5. "f.dirty" is true iff the buffer contains bytes that
           should be flushed to the file; by V3 and V4, only part
           of the buffer can be dirty.

           f.dirty ==
           (exists i in [f.lo, f.curr):
             c(f)[i] != f.buff[i - f.lo])

       V6. this.maxHi == this.lo + this.buff.length
       
       Note that "f.buff" can be "null" in a valid file, since the
       range of characters in V3 is empty when "f.lo == f.curr".
       
       A file is said to be *ready* if the buffer contains the
       current position, i.e., when:
       
       R1. !f.closed && f.buff != null
           && f.lo <= f.curr && f.curr < f.hi
           
       When a file is ready, reading or writing a single byte
       can be performed by reading or writing the in-memory
       buffer without performing a disk operation.
    */

    /** Open a new <code>BufferedRandomAccessFile</code> on
        <code>file</code> in mode <code>mode</code>, which
        should be "r" for reading only, or "rw" for reading
        and writing. */
    public BufferedRandomAccessFile(File file, String mode)
      throws IOException {
        super(file, mode);
        this.init();
    }

    private final void Assert(boolean cond) {
        if (!cond)
            throw new InternalError("Assertion failed in BufferedRandomAccessFile");
    }
    
    /** Open a new <code>BufferedRandomAccessFile</code> on the
        file named <code>name</code> in mode <code>mode</code>, 
        which should be "r" for reading only, or "rw" for reading
        and writing. */
    public BufferedRandomAccessFile(String name, String mode)
      throws IOException {
        super(name, mode);
        this.init();
    }
    
    /* Initialize the private fields of the file so as to
       make it valid. */
    private void init() {
        this.dirty = this.closed = false;
        this.lo = this.curr = this.hi = 0;
        synchronized (mu) {
            this.buff = (numAvailBuffs > 0)
              ? availBuffs[--numAvailBuffs]
              : new byte[BuffSz];
        }
        this.maxHi = (long) BuffSz;
        this.hitEOF = false;
        this.diskPos = 0L;
    }
    
    public void close() throws IOException {
        Assert(!this.closed);
        this.flush();
        this.closed = true;
        synchronized (mu) {
            // grow "availBuffs" array if necessary
            if (numAvailBuffs >= availBuffs.length) {
                byte[][] newBuffs = new byte[numAvailBuffs + 10][];
                System.arraycopy(availBuffs, 0, newBuffs, 0, numAvailBuffs);
                availBuffs = newBuffs;
            }
            availBuffs[numAvailBuffs++] = this.buff;
        }
        super.close();
    }
    
    /** Flush any bytes in the file's buffer that have not
        yet been written to disk. If the file was created
        read-only, this method is a no-op. */
    public void flush() throws IOException {
        // Assert.P(!this.closed);
        this.flushBuffer();
    }
    
    /* Flush any dirty bytes in the buffer to disk. */
    private void flushBuffer() throws IOException {
        if (this.dirty) {
            Assert(this.curr > this.lo);
            if (this.diskPos != this.lo) super.seek(this.lo);
            int len = (int)(this.curr - this.lo);
            super.write(this.buff, 0, len); 
            this.diskPos = this.curr;
            this.dirty = false;
        }
    }
    
    /* Read at most "this.buff.length" bytes into "this.buff",
       returning the number of bytes read. If the return result
       is less than "this.buff.length", then EOF was read. */
    private int fillBuffer() throws IOException {
        int cnt = 0;
        int rem = this.buff.length;
        while (rem > 0) {
            int n = super.read(this.buff, cnt, rem); 
            if (n < 0) break;
            cnt += n;
            rem -= n;
        }
        this.hitEOF = (cnt < this.buff.length);
        this.diskPos += cnt;
        return cnt;
    }

    /* This method positions <code>this.curr</code> at position
       <code>pos</code>. If <code>pos</code> does not fall in the
       current buffer, it flushes the current buffer and loads
       the correct one.<p>
       
       On exit from this routine <code>this.curr == this.hi</code>
       iff <code>pos</code> is at or past the end-of-file, which
       can only happen if the file was opened in read-only mode. */
    public void seek(long pos) throws IOException {
        // Assert.P(!this.closed);
        if (pos >= this.hi || pos < this.lo) {
            // seeking outside of current buffer -- flush and read
            this.flushBuffer();
            this.lo = pos & BuffMask; // start at BuffSz boundary
            this.maxHi = this.lo + (long) this.buff.length;
            if (this.diskPos != this.lo) {
                super.seek(this.lo); 
                this.diskPos = this.lo;
            }
            int n = this.fillBuffer();
            this.hi = this.lo + (long) n;
        } else {
            // seeking inside current buffer -- no read required
            if (pos < this.curr) {
                // if seeking backwards, we must flush to maintain V4
                this.flushBuffer();
            }
        }
        this.curr = pos;
    }

    public long getFilePointer() {
        // Assert.P(!this.closed);
        return this.curr;
    }
    
    public long length() throws IOException {
        // Assert.P(!this.closed);
        return Math.max(this.curr, super.length());
    }
    
    public int read() throws IOException {
        // Assert.P(!this.closed);
        if (this.curr == this.hi) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hitEOF) return -1;
                
            // slow path -- read another buffer
            this.seek(this.curr);
            if (this.curr == this.hi) return -1;
        }
        // Assert.P(this.curr < this.hi);
        byte res = this.buff[(int)(this.curr - this.lo)];
        this.curr++;
        return ((int)res) & 0xFF; // convert byte -> int
    }
    
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        // Assert.P(!this.closed);
        if (this.curr == this.hi) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hitEOF) return -1;
                
            // slow path -- read another buffer
            this.seek(this.curr);
            if (this.curr == this.hi) return -1;
        }
        Assert(this.curr < this.hi);
        len = Math.min(len, (int)(this.hi - this.curr));
        int buffOff = (int)(this.curr - this.lo);
        System.arraycopy(this.buff, buffOff, b, off, len);
        this.curr += len;        
        return len;
    }
    
    public void write(int b) throws IOException {
        // Assert.P(!this.closed);
        if (this.curr == this.hi) {
            if (this.hitEOF && this.hi < this.maxHi) {
                // at EOF -- bump "hi"
                this.hi++;
            } else {
                // slow path -- write current buffer; read next one
                this.seek(this.curr);
                if (this.curr == this.hi) {
                    // appending to EOF -- bump "hi"
                    Assert(this.hitEOF);
                    this.hi++;
                }
            }
        }
        Assert(this.curr < this.hi);
        this.buff[(int)(this.curr - this.lo)] = (byte)b;
        this.curr++;
        this.dirty = true;
    }
    
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(byte[] b, int off, int len)
      throws IOException {
        // Assert.P(!this.closed);
        while (len > 0) {
            int n = this.writeAtMost(b, off, len);
            off += n;
            len -= n;
        }
        this.dirty = true;
    }
    
    /* Write at most "len" bytes to "b" starting at position "off",
       and return the number of bytes written. */
    private int writeAtMost(byte[] b, int off, int len)
      throws IOException {
        if (this.curr == this.hi) {
            if (this.hitEOF && this.hi < this.maxHi) {
                // at EOF -- bump "hi"
                this.hi = this.maxHi;
            } else {
                // slow path -- write current buffer; read next one
                this.seek(this.curr);
                if (this.curr == this.hi) {
                    // appending to EOF -- bump "hi"
                    Assert(this.hitEOF);
                    this.hi = this.maxHi;
                }
            }
        }
        Assert(this.curr < this.hi);
        len = Math.min(len, (int)(this.hi - this.curr));
        int buffOff = (int)(this.curr - this.lo);
        System.arraycopy(b, off, this.buff, buffOff, len);
        this.curr += len;
        return len;
    }
    
// Extensions for storing numbers and strings more compactly.
// Based on Oberon's Files.Mod (original idea by Martin Odersky).

    public void writeNum(long x) throws IOException {
        while (x < -64 || x > 63) {
            write((int)( (x & 0x7F) | 0x80));
            x = x >> 7;
        }
        write((int)(x & 0x7F));
    }
    
    public long readNum() throws IOException  {
        int s = 0;
        long n = 0;
        long ch = read();
        while (ch >= 128) {
            n |= (ch & 0x7F) << s;
            s += 7;
            ch = read();
        }
        n |= (ch % 64 - ch / 64 * 64) << s;
        return n;
    }
    
/*
Read and write UTF strings where the length of the string is variable length encoded.
*/    
    
    public final void writeUTFx(String str) throws IOException {
    	int strlen = str.length();
    	int utflen = 0;

    	for (int i = 0 ; i < strlen ; i++) {
    	    int c = str.charAt(i);
    	    if ((c >= 0x0001) && (c <= 0x007F)) {
    		    utflen++;
    	    } else if (c > 0x07FF) {
    		    utflen += 3;
    	    } else {
    		    utflen += 2;
    	    }
    	}

        writeNum(utflen);
    	for (int i = 0 ; i < strlen ; i++) {
    	    int c = str.charAt(i);
    	    if ((c >= 0x0001) && (c <= 0x007F)) {
    		    write(c);
    	    } else if (c > 0x07FF) {
        		write(0xE0 | ((c >> 12) & 0x0F));
        		write(0x80 | ((c >>  6) & 0x3F));
        		write(0x80 | ((c >>  0) & 0x3F));
    		    //written += 2;
    	    } else {
        		write(0xC0 | ((c >>  6) & 0x1F));
        		write(0x80 | ((c >>  0) & 0x3F));
        		//written += 1;
    	    }
    	}
    }
    
    public final String readUTFx() throws IOException {
        int utflen = (int)readNum();
        char str[] = new char[utflen];
    	int count = 0;
    	int strlen = 0;
    	while (count < utflen) {
    	    int c = read();
    	    int char2, char3;
    	    switch (c >> 4) { 
    	        case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
        		    // 0xxxxxxx
        		    count++;
        		    str[strlen++] = (char)c;
        		    break;
    	        case 12: case 13:
        		    // 110x xxxx   10xx xxxx
        		    count += 2;
        		    char2 = read();
        		    str[strlen++] = (char)(((c & 0x1F) << 6) | (char2 & 0x3F));
        		    break;
    	        case 14:
        		    // 1110 xxxx  10xx xxxx  10xx xxxx
        		    count += 3;
        		    char2 = read();
        		    char3 = read();
        		    str[strlen++] = (char)(((c & 0x0F) << 12) |
        					   ((char2 & 0x3F) << 6) |
        					   ((char3 & 0x3F) << 0));
        		    break;
    	        default:
        		    // 10xx xxxx,  1111 xxxx
        		    throw new UTFDataFormatException();		  
    		}
    	}
        return new String(str, 0, strlen);
    }    
    
}
