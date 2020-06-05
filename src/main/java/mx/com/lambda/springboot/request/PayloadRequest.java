package mx.com.lambda.springboot.request;

public class PayloadRequest {

	private String idProducto;
	private String idDepartemanto;

	public String getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(String idProducto) {
		this.idProducto = idProducto;
	}
	public String getIdDepartemanto() {
		return idDepartemanto;
	}
	public void setIdDepartemanto(String idDepartemanto) {
		this.idDepartemanto = idDepartemanto;
	}
}
