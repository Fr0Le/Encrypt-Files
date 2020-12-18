package ru.fr0le.encryptFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

//Источник - зашифровать и расшифровать файлы с помощью DES
//http://www.avajava.com/tutorials/lessons/how-do-i-encrypt-and-decrypt-files-using-des.html
public class Main {	

	private static String dir;
	private static String oldFormat;
	private static String newFormat;
	private static String key;
	private static int countModels;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		while(true) {
			System.out.print("Enter dir models (example - models): ");		
			dir = scanner.nextLine();			
			File checkDir = new File(dir);
			if(checkDir.isDirectory()) break;
			else System.out.println("Dir not found!");		
		}		

		while(true) {
			System.out.print("Enter old models format (example - obj): ");		
			oldFormat = scanner.nextLine();
			if(oldFormat.isEmpty() || oldFormat.contains(" ")) System.out.println("Incorrect format!");
			else break;
		}

		while(true) {
			System.out.print("Enter new models format (example - hellz): ");		
			newFormat = scanner.nextLine();
			if(newFormat.isEmpty() || newFormat.contains(" ")) System.out.println("Incorrect format!");
			else break;
		}

		while(true) {
			System.out.print("Enter key minimum 8 characters (example - hellzGuard123): ");		
			key = scanner.nextLine();
			if(key.isEmpty() || key.length() < 8) System.out.println("Incorrect format!");
			else break;
		}

		scanner.close();

		System.out.println("===== Procces =====");
		processFilesFromFolder(new File(dir));		

		System.out.println("===== Result =====");
		System.out.println("Models encrypted - " + countModels);
		System.out.println("Dir models - " + dir);
		System.out.println("Old format - " + oldFormat);
		System.out.println("New format - " + newFormat);
		System.out.println("Key - " + key);
	}

	public static void processFilesFromFolder(File folder) {
		File[] folderEntries = folder.listFiles();
		for (File entry : folderEntries) {	    	
			if(entry.isDirectory())  {
				processFilesFromFolder(entry);
				continue;
			}      

			if(entry.isFile()) {
				if(entry.getName().substring(entry.getName().lastIndexOf(".")).equals("." + oldFormat)) {
					try {
						FileInputStream fis = new FileInputStream(entry);
						String newName = entry.getName().substring(0, entry.getName().lastIndexOf(".")) + ("." + newFormat);
						FileOutputStream fos = new FileOutputStream(entry.getParentFile() + "/" + newName);
						System.out.println(entry.getName() + " => " + newName);
						countModels++;
						encrypt(key, Cipher.ENCRYPT_MODE, fis, fos);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}	        
		}
	}

	public static void encrypt(String key, int mode, InputStream is, OutputStream os) throws Throwable {
		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, desKey);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		doCopy(cis, os);
	}

	public static void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}
}