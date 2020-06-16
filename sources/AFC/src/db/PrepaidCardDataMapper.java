package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import certificate.PrepaidCard;
import certificate.TicketDBGateway;

public class PrepaidCardDataMapper implements TicketDBGateway{
	
	@Override
	public PrepaidCard getCertificateById(String id) throws SQLException, ClassNotFoundException {
		Connection connection = ConnectToMySQL.getInformation("travelling_certificate");
		Statement statement = connection.createStatement();
		String sql = "Select * from certificate_info WHERE id='" + id +"'";
		ResultSet rs = statement.executeQuery(sql);
		PrepaidCard result = null;
		if (rs != null) {
			while(rs.next()) {
				int type = rs.getInt(2);
				if (type != Config.PREPAID_TYPE) {
					System.out.println("This is not a prepaid card!");
					return null;
				}
				result = new PrepaidCard(id, getBalance(id));
			}
		}
		connection.close();
		return result;
	}
	
}
