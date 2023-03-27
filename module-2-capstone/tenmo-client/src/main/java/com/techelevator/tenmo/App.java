package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.*;


import java.math.BigDecimal;
import java.util.Scanner;


public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            userService.setAuthToken(currentUser.getToken());
            transferService.setAuthToken(currentUser.getToken());
            accountService.setAuthToken(currentUser.getToken());
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        System.out.println("Account Balance: " + accountService.getAccountBalance(currentUser.getUser().getId()));

    }

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransfersFromUserId(currentUser, currentUser.getUser().getId());
        System.out.println("\n" + "╔.★. .═════════════════════════════╗");
        System.out.println("Transfers");
        System.out.println("ID     From/To          Amount");
        System.out.println("╚═════════════════════════════. .★.╝"+ "\n");
        int recipientId = 0;

        int currentUserAccountId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        for(Transfer transfer: transfers) {
            printTransferStubDetails(currentUser, transfer);
        }

        System.out.println(("\nPlease enter transfer ID to view details (0 to cancel)"));
        Scanner input = new Scanner(System.in);
        if (input.nextInt() != currentUser.getUser().getId()){
            recipientId = input.nextInt();
        }

        Transfer transferChoice = validateTransferIdChoice(recipientId, transfers, currentUser);
        if(transferChoice != null) {
            int fromAccount = transferChoice.getAccountFrom();
            int toAccount = transferChoice.getAccountTo();
            int fromUserId = accountService.getAccountById(currentUser, fromAccount).getUserId();
            int toUserId = accountService.getAccountById(currentUser, toAccount).getUserId();

            System.out.println("\n" + "╔.★. .═════════════════════════════╗");
            System.out.println("Transfer Details");
            System.out.println("╚═════════════════════════════. .★.╝"+ "\n");
            System.out.println("Id: " + transferChoice.getTransferId());
            System.out.println("From: " + userService.getUserById(currentUser, fromUserId).getUsername());
            System.out.println("To: " + userService.getUserById(currentUser, toUserId).getUsername());
            System.out.println("Type: " + transferChoice.getTransferTypeId());
            System.out.println("Status: " + transferChoice.getTransferStatusId() );
            System.out.println("Amount: $" + transferChoice.getAmount());
        }
	}
    private Transfer validateTransferIdChoice(int transferIdChoice, Transfer[] transfers, AuthenticatedUser currentUser) {
        Transfer transferChoice = null;
        if(transferIdChoice != 0) {
            boolean validTransferIdChoice = false;
            for (Transfer transfer : transfers) {
                if (transfer.getTransferId() == transferIdChoice) {
                    validTransferIdChoice = true;
                    transferChoice = transfer;
                    break;
                }
            }
            if (!validTransferIdChoice) {
                System.out.println("Invalid choice");;
            }
        }
        return transferChoice;
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        //Optional
        System.out.println("not implemented");
    }

	private void sendBucks() {
        // TODO Auto-generated method stub

        // used when we list out our snacks
        System.out.println("\n" + "╔.★. .═════════════════════════════╗");
        System.out.println("       List Of All Our Users");
        System.out.println("╚═════════════════════════════. .★.╝"+ "\n");
        int recipientId = 0;
        User[] users = userService.getListOfUsers();
        for(User user: users) {
            System.out.println(user.getId() + "          " + user.getUsername());
        }
        System.out.println("---------");
        System.out.flush();
        System.out.print("Please enter recipient: ");
        Scanner input = new Scanner(System.in);
        recipientId = input.nextInt();

        while (recipientId != currentUser.getUser().getId() && recipientId != 0 && userService.getListOfUsers().equals(recipientId)) {
            if (recipientId == currentUser.getUser().getId())
                System.out.println("You can't send money to yourself. Please try again.");
            else if (recipientId == 0 || !userService.getListOfUsers().equals(recipientId))
                System.out.println("Please enter a valid userID and try again.");
            else
                System.out.println("UserID received.");
        }
    }
    private void requestBucks() {
        // TODO Auto-generated method stub
        //Optional
        System.out.println("not implemented");
    }
    public String printTransfers(int transferId, String fromOrTo, BigDecimal amount) {
        return transferId + "     " + fromOrTo + "          " + "$ " + amount;

    }

    private void printTransferStubDetails(AuthenticatedUser authenticatedUser, Transfer transfer) {
        String fromOrTo = "";
        int accountFrom = transfer.getAccountFrom();
        int accountTo = transfer.getAccountTo();
        if (accountService.getAccountById(currentUser, accountTo).getUserId() == authenticatedUser.getUser().getId()) {
            int accountFromUserId = accountService.getAccountById(currentUser, accountFrom).getUserId();
            String userFromName = userService.getUserById(currentUser, accountFromUserId).getUsername();
            fromOrTo = "From: " + userFromName;
        } else {
            int accountToUserId = accountService.getAccountById(currentUser, accountTo).getUserId();
            String userToName = userService.getUserById(currentUser, accountToUserId).getUsername();
            fromOrTo = "To: " + userToName;
        }

        System.out.print(printTransfers(transfer.getTransferId(), fromOrTo, transfer.getAmount()));
    }

}
