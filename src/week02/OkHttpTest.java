package week02;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpTest {
	public static void main(String[] args) {
		OkHttpClient client = new OkHttpClient();

		try {
			Request request = new Request.Builder().url("http://localhost:8801").build();
			Response response = client.newCall(request).execute();
			System.out.println(response.body().string());
			response.body().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}

	}
}
