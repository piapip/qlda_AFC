package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import certificate.PrepaidCard;
import certificate.TicketDBGateway;

/**
 * The Class PrepaidCardDataMapper.
 */
public class PrepaidCardDataMapper implements TicketDBGateway{
	
	/**
	 * Gets the certificate by id.
	 *
	 * @param id the id
	 * @return the certificate by id
	 * @throws SQLException the SQL exception
	 * @throws ClassNotFoundException the class not found exception
	 */
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
	
	/**
	 * Gets the balance.
	 *
	 * @param id the id
	 * @return the balance
	 * @throws SQLException the SQL exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	private double getBalance(String id) throws SQLException, ClassNotFoundException {
		Connection connection = ConnectToMySQL.getInformation("travelling_certificate");
		Statement statement = connection.createStatement();
		String sql = "Select * from prepaid_card WHERE id='" + id + "'";
		ResultSet rs = statement.executeQuery(sql);
		double result = 0;
		if(rs != null) {
			while(rs.next()){
				result = rs.getDouble(2);
			}
		}
		connection.close();
		return result;
	}
	
}
