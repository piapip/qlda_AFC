package view;

import java.sql.SQLException;
import java.util.Scanner;

import controller.Controller;
import db.HistoryDataMapper;
import db.HistorySaver;
import db.Hour24TicketDataMapper;
import db.Hour24TicketUpdater;
import db.OnewayTicketDataMapper;
import db.OnewayTicketUpdater;
import db.PrepaidCardDataMapper;
import db.PrepaidCardUpdater;
import db.StationDataMapper;
import hust.soict.se.customexception.InvalidIDException;
import hust.soict.se.gate.Gate;
import hust.soict.se.recognizer.TicketRecognizer;
import hust.soict.se.scanner.CardScanner;
import requirements.RequirementHour24Ticket;
import requirements.RequirementOnewayTicket;
import requirements.RequirementPrepaidCard;
import station.StationDistanceByDistance;

public class Main {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 * @throws InvalidIDException the invalid ID exception
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, InvalidIDException, InterruptedException {
		int stationId;
		int scanOption;
		int enterOrExit;
		String error;
		Controller controller;
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);
		Gate gate = Gate.getInstance();
		do {
			do {
				System.out.println("Station ID: ");
				stationId = reader.nextInt();
				reader.nextLine();
			} while(stationId > 9 || stationId < 1);
			
			do {
				System.out.println("Enter or Exit?:\n1. Enter\n2. Exit ");
				enterOrExit = reader.nextInt();
				reader.nextLine();
			} while(enterOrExit > 2 || enterOrExit < 1);
			
			do {
				System.out.println("What are you using?:\n1. Card\n2. One way Ticket\n3. 24-hour Ticket");
				scanOption = reader.nextInt();
				reader.nextLine();
			} while(scanOption > 3 || scanOption < 1);
			
			if(scanOption == 2) 
				controller = new Controller(
						new RequirementOnewayTicket(new OnewayTicketDataMapper(), new HistoryDataMapper()), 
						new OnewayTicketUpdater(), 
						new StationDistanceByDistance(new StationDataMapper()), 
						new HistorySaver(),
						new HistoryDataMapper());
			else if (scanOption == 3) 
				controller = new Controller(
						new RequirementHour24Ticket(new Hour24TicketDataMapper(), new HistoryDataMapper()),
						new Hour24TicketUpdater(),
						new StationDistanceByDistance(new StationDataMapper()),
						new HistorySaver(),
						new HistoryDataMapper());
			else 
				controller = new Controller(
						new RequirementPrepaidCard(new PrepaidCardDataMapper(), new HistoryDataMapper()), 
						new PrepaidCardUpdater(), 
						new StationDistanceByDistance(new StationDataMapper()), 
						new HistorySaver(),
						new HistoryDataMapper());
			
			System.out.println("Please enter your barcode: ");
			String barCode = reader.nextLine();
			
			TicketRecognizer ticketRecognizer = TicketRecognizer.getInstance();
			CardScanner cardScanner = CardScanner.getInstance();
			
			String certificateId;
			if(scanOption == 1) {
				certificateId = cardScanner.process(barCode);
			} else {
				certificateId = ticketRecognizer.process(barCode);
			}
			
			if(enterOrExit == 1) error = controller.enter(certificateId, stationId);
			else error = controller.exit(certificateId, stationId);
			
			if(error == null) {
				gate.open();
				Thread.sleep(2000);
				gate.close();
			} else {
				System.out.println("ERRRRRRR!");
				System.out.println(error);
			}
			
		} while(true);
	}
}
