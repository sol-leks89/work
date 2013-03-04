package net.brakingpoint;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FeedReader extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String msg = "Hot Dang!";
		GsonExperiment ge = new GsonExperiment();
		Gson gson = new GsonBuilder().create();
		gson.toJson(ge, out);


		out.flush();

	}

}
