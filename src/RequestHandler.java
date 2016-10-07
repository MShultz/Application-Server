import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandler implements Runnable {
	String request;
	Server serv;
	String responseHeader;
	Socket clientSocket;
	Calculator calc;

	public RequestHandler(String request, Server serv, Socket clientSocket, Calculator calc) {
		this.request = request;
		this.serv = serv;
		this.clientSocket = clientSocket;
		this.calc = calc;
	}

	@Override
	public void run() {
		byte[] request = parseRequest();
		serv.respond(responseHeader, request, clientSocket);
	}

	private byte[] parseRequest() {
		byte[] response = null;
		if (request.contains("calc")) {
			try {
				double answer = parseCalculation(request);
				if(responseHeader.contains("200"))
				response = formatCalculationResponse(answer).getBytes("ASCII");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			int firstIndex = request.indexOf("/");
			try {
				response = getFileResponse(request.substring(firstIndex, request.indexOf(" ", firstIndex + 1)).trim());
			} catch (StringIndexOutOfBoundsException e) {
				responseHeader = "HTTP/1.0 404 Not Found\r\n";
			}
		}
		if (responseHeader == null)
			setResponseHeader(response == null);
		return response;
	}

	private void setResponseHeader(boolean responseIsNull) {
		if (responseIsNull)
			responseHeader = "HTTP/1.0 404 Not Found\r\n";
		else
			responseHeader = "HTTP/1.0 200 OK\r\n";
	}

	private byte[] getFileResponse(String pageRequested) {
		byte[] response = null;
		try {
			response = Files.readAllBytes(Paths.get("src/My Website" + pageRequested));
		} catch (IOException e) {
			responseHeader = "HTTP/1.0 404 Not Found\r\n";
		}
		return response;
	}

	private double parseCalculation(String request) {
		String calcType = request.substring(request.indexOf("calc/") + 5, request.indexOf("?"));
		double value = 0;
		double[] values = parseValues(request);
		if (responseHeader == null || !responseHeader.contains("500")) {
			responseHeader = "HTTP/1.0 200 OK\r\n";
			System.out.println(calcType);
			switch (calcType) {
			case "add":
				value = calc.add(values[0], values[1]);
				break;
			case "subtract":
				value = calc.subtract(values[0], values[1]);
				break;
			case "multiply":
				value = calc.multiply(values[0], values[1]);
				break;
			case "divide":
				value = calc.multiply(values[0], values[1]);
				break;
			default:
				responseHeader = "HTTP/1.0 404 Not Found\r\n";
			}
			System.out.println(responseHeader);
		}
		return value;
	}

	private double[] parseValues(String request) {
		double[] values = new double[2];
		try {
			values[0] = Double.valueOf(request.substring(request.indexOf("=") + 1, request.indexOf("&")));
			values[1] = Double.valueOf(request.substring(request.indexOf("y=") + 2, request.indexOf("HTTP")).trim());
		} catch (Exception e) {
			responseHeader = "HTTP/1.0 500 Internal Error\r\n";
		}
		return values;
	}

	private String formatCalculationResponse(double answer) {
		return "<!DOCTYPE html><html><body><p> The answer is: " + answer + "!</body></html>";

	}

}
