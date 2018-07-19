Android Bitcoin wallet
========================================

This application is a class project for the undergraduate Syracuse University class CIS 444 Mobile Application Programming in spring 2015.  We deployed this android application to both a phone and a tablet.  As part of our demonstration we transferred testnet bitcoin between the two devices using QR code scanning.

This project will be an Android application that implements a Bitcoin wallet.  This application will allow a user to send and receive bitcoins.  Bitcoins are units of measure on a global public ledger (blockchain) that may be exchanged for local currencies.  A user will be able to generate one or more unique addresses within the Bitcoin blockchain and then add the address or addresses to a wallet (collection of public/private key pairs) stored in local file system storage on the device.  Each address is a hash of the public key from a public/private key pair and each wallet consists of a collection of these key pairs.  I will use the [bitcoinj](https://github.com/bitcoinj) Java library to implement the Bitcoin specific functions such as cryptography and interacting with the public distributed blockchain.
  
This application will consist of 16 views as presented in the attached Diagram 1.  All views will honor the default Android back button navigation as well as a left swipe gesture to navigate backwards on the stack.

![](cis444_android_wallet_ss.png?raw=true)

To-do
-----

- [X] View 1: Welcome
  This view will launch at application start, it will present a welcome page with an "about" snippet.  A tap, right swipe, or waiting 2 seconds will navigate to view 2.
  
- [X] View 2: Select Wallet
  This view will give the use the opportunity to either select an existing wallet stored in the local file system or to create a new wallet.  Selecting the create wallet option will navigate to view 3.  Selecting an existing wallet from a drop down navigation element will navigate to view 5.
  
- [X] View 3: Create Wallet
  This view allows for entering a wallet name to identify a new wallet.  Navigation from here is to either to view 4 to add addresses to the wallet, or to view 5 to view the wallet.

- [X] View 4: Create Address
  This view allows for selecting whether the user wishes to create a new address key pair via navigation to view 15 or add an existing address to the wallet via navigation to view 16.
  
- [X] View 5: Wallet Summary
  This view allows for selecting which more detailed view of the current wallet you wish to use.  Selecting balance will navigate to view 6.  Selecting Send will navigate to view 8.  Selecting Transactions will navigate to view 7.  Selecting Edit will navigate to view 4.
  
- [X] View 6: Wallet Balance
  This view will show to sum of US dollars that the bitcoins in the wallet would generate if sold an exchange.  Only back navigation is allowed from this view.
  
- [X] View 7: Wallet Transactions
  This view will display the past blockchain transactions for addresses in this wallet.  Only back navigation is allowed from this view.
  
- [X] View 8: Send
  This view allows selecting the method of entry of an recipient to send bitcoin to.  An recipient may be directly entered (pasted) on this page and then navigate to view 12. Selecting Scan QR code will navigate to view 11.  Selecting Select Recipient from address book will navigate to view 9.
  
- [X] View 9: Address Book
  This view is a listing of addresses that are optionally identified by local names.  Selecting an address will navigate to view 10.
  
- [X] View 10: Address
  This view displays an address, and its name.  Selecting Transactions navigates to view 13. Selecting Send navigates to view 12. Selecting View QR will navigate to view 14.
  
- [X] View 11: QR Scan
  This view uses the camera to capture a QR image.  On completion this view will return to the calling view which is view 4 or view 8.
  
- [X] View 12: Send
  This view allows for entering an amount in bitcoin or USD equivalent value.  Selecting Send will send the specified amount of bitcoin to the recipient address and then navigate to view 7.

- [X] View 13: Address Details
  This view will list the balance and transactions for a specific address recorded in the blockchain.
  
- [X] View 14: View QR
  This view will display a QR code for the selected address.  Only back navigation is allowed from this view.
  
- [X] View 15: Create Address Entry
  This view collects a name and optional parameters when creating a new address.  It navigates back to view 4.
  
- [X] View 16: Add Address to Wallet
An address may be directly entered (pasted) on this page and then navigate to view 5. Selecting Scan QR code will navigate to view 11.
