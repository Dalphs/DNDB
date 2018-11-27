# DNDB

## Methoder

* deposit indsætter et beløb på en specifik konto og opdaterer alle steder kontoens status gøres op.
Derudover opdateres tabellerne transactions og deposits.

* withdraw fratrækker et beløb fra en specifik konto og opdaterer alle steder kontoens status gøres op.
Derudover opdateres tabellerne transactions og withdrawals.

* transfer trækker et beløb fra en specifik konto og indsætter det på en anden specifik konto. 
Derudover opdateres alle stede kontiernes status gøres op.
Tabellerne transactions og deposits opdateres.

* addInterest tilføjer renter til balance på en specifik konto og opdatere alle steder konotens status gøres op. Metoden fungerer både på opsparing og lån, da lån fremgår som negative tal

* createUser opretter en ny bruger i systemet enten ansat eller kunde med tilfældig navn og adresse

* editUser kan ændre brugerens navn i tilfælde af navneforandring

* deleteUser kan slette en brguer ud fra dens customer_id
