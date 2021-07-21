package com.revature.menu;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.BankObject;
import com.revature.beans.User;
import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

// Encapsulate the user interface methods
public class Menu {

	private static final Logger log = LogManager.getLogger(Menu.class);
	
	private UserService us = new UserService();
	private User loggedUser = null;
	private Scanner scan = SingletonScanner.getScanner().getScan();
	
	public void start() {
		log.trace("Start BankApp. start()");
		mainLoop: while(true) {
			switch(startMenu()) {
			case 1:
				// login
				System.out.println("Please enter your username: ");
				String username = scan.nextLine();
				log.debug(username);
				// Call the user service to find the user we want.
				User u = us.login(username);
				if(u == null) {
					log.warn("Unsuccessful login attempt: "+ username);
					System.out.println("Please try again.");
				} else {
					loggedUser = u;
					System.out.println("Welcome back: "+u.getUsername());
					// call our next method (either the Member menu or the Associate menu, depending on user)
					log.info("Successful login for user: "+loggedUser);
					switch(loggedUser.getType()) {
					case MEMBER:
						member();
						break;
					case ASSOCIATE:
						associate();
						break;
					}
				}
				break;
			case 2:
				System.out.println("Choose your username: ");
				String newName = scan.nextLine();
				if(!us.checkAvailability(newName)) {
					System.out.println("Username not available, please try again.");
					continue mainLoop;
				}
				System.out.println("Enter your email address: ");
				String email = scan.nextLine();
				System.out.println("enter your birthday (YYYY/MM/DD): ");
				List<Integer> bday = Stream.of(scan.nextLine().split("/"))
						.map((str)->Integer.parseInt(str)).collect(Collectors.toList());
				
				LocalDate birth = LocalDate.of(bday.get(0), bday.get(1), bday.get(2));
				if(!us.checkBirthday(birth)) {
					System.out.println("Not old enough, please try again when you are older.");
					continue mainLoop;
				}
				System.out.println("Registering...");
				us.register(newName, email, birth);
				break;
			case 3:
				// quit
				System.out.println("Goodbye!");
				break mainLoop;
			default:
				// invalid selection
				System.out.println("Not a valid selection, please try again.");
			}
		}
		log.trace("Ending start()");
	}
	
	private int startMenu() {
		log.trace("called startMenu()");
		System.out.println("Welcome to BankApp!");
		System.out.println("What would you like to do?");
		System.out.println("\t1. Login");
		System.out.println("\t2. Register");
		System.out.println("\t3. Quit");
		int selection = select();
		log.trace("Start menu returning selection: "+selection);
		return selection;
	}
	
	private void member() {
		log.trace("called member()");
		player: while(true) {
			switch(memberMenu()) {
			case 1:
				// daily bonus
				if(us.hasCheckedIn(loggedUser)) {
					System.out.println("Already checked in today, please try again tomorrow!");
					break;
				}
				us.doCheckIn(loggedUser);
				System.out.println("You gained $"+BankObject.DAILY_BONUS);
				System.out.println("Your new total is $"+loggedUser.getCurrency());
				break;
			case 2:
				// view currency
				System.out.println("You currently have $"+loggedUser.getCurrency());
				break;
			case 3:
				// spend currency
				break;
			case 4:
				loggedUser = null;
				break player;
			default:
			}
		}
	}
	
	private int memberMenu() {
		System.out.println("What would you like to do?");
		System.out.println("\t1. Deposit");
		System.out.println("\t2. Check Balance");
		System.out.println("\t3. Make Withdrawal");
		System.out.println("\t4. Logout");
		return select();
	}
	private void associate() {
		associate: while(true) {
			
		}
	}
	
	
	private int select() {
		int selection;
		try {
			selection = Integer.parseInt(scan.nextLine());
		} catch(Exception e) {
			selection = -1;
		}
		//log
		return selection;
	}

}