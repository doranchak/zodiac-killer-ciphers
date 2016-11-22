import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Adobe {
	public static void search(String path) {
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				if (counter % 100000 == 0)
					System.out.println("Read " + counter + " lines...");
				if (line.contains("oranchak")
						|| line.contains("Nz4/TI6o5RrioxG6CatHBw=="))
					System.out.println(line);
			}
			// System.out.println("read " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		search("/Users/doranchak/Downloads/cred");
	}
}
