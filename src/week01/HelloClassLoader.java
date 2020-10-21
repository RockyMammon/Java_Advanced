package week01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 *created by luo hao
 *
 * @package week01
 * @date 2020/20/21
 */

public class HelloClassLoader extends ClassLoader{

	//define some variables
	private static final String FILEPATH = "Hello.xlass";
	private static final String FILENAME = "Hello";
	private static final String METHODNAME = "hello";
	
	
	//define findClass with two input parameters
	public Class findClass(String name, String filePath) {
		byte[] byteArray = new byte[0];
		try {
			File file = new File(filePath);
			InputStream inputStream = new FileInputStream(file);
			byteArray = new byte[inputStream.available()];
			int val = inputStream.read();
			int index = 0;
			while(val != -1) {
				byteArray[index++] = (byte) (255 - val);
				val = inputStream.read();
			}	

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return defineClass(name, byteArray, 0, byteArray.length);
	}
	
	
	public static void main(String [] args) {
		HelloClassLoader helloClassLoader = new HelloClassLoader();
		Class helloClass = helloClassLoader.findClass(FILENAME, FILEPATH);
		try {
			//reflect to get method name
			Method helloMethod = helloClass.getMethod(METHODNAME, null);
			helloMethod.invoke(helloClass.newInstance());
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |InstantiationException e) {

			e.printStackTrace();
		} 
		
		
	}
}
