import java.net.MalformedURLException;

public class Main {


    public static void main(String[] args) {
        DBConnect connect = new DBConnect();

        //Her er eksempler på brug af mine metoder til hver delopgave.
        //Der står også hvilken linje metoderne kan findes på i DBConnect-klassen

        //1. Indbetaling, skal bruge et beløb og et konto-ID (Linje 29)
        //connect.deposit(100, 1);

        //2. Kontanthævning, Skal bruge et beløb og et konto-ID (linje 48)
        //connect.withdraw(100, 1);

        //3. Overførsel, Skal bruge et beløb og et konto-ID for senderen og et for modtageren (Linje 75)
        //connect.transfer(100, 1, 2);

        //4-5. Rentetilskrivning og rentetrækning 'y' er årlig, 'm' er månedlig og 'd' er daglig
        //Fungerer både som tilskrivning og trækning da lån er bogført med negative tal (Linje 105)
        //connect.addInterests(4, 'y');
        //connect.addInterests(4, 'm');
        //connect.addInterests(4, 'd');

        //6. Oprettelse af bruger, i dette tilfælde en auotgeneret. 0 som parameter generere en kunde
        // og 1 generere en ansat (Linje 129)
        //connect.createUser(0);
        //connect.createUser(1);

        //7. Rettelse af bruger, i dette tilfælde rettelse af brugerens navn. Denne metode tager imod to parametre,
        //et kunde-ID til identifikation af den rigtige bruger og en String som indeholder det nye navn (Linje 147)
        //connect.editUser(1, "Thomas Danielsen");

        //8. Brugersletning af en kunde ud fra kunde-ID,i dette tilfælde bliver brugeren med kunde-ID 5 slettet (Linje 157)
        //connect.deleteUser(5);

        //9. Visning af alle brugere, a = alfabetisk, d = kronologisk, s = samlet opsparing, l = samlet lån (Linje 169)
        //connect.getUsers('a');
        //connect.getUsers('d');
        //connect.getUsers('s');
        //connect.getUsers('l');

        //10. visning af alle konti, a = alfabetisk, d = kronologisk, s = samlet opsparing, l = samlet lån (Linje 215)
        //connect.getAccounts('a');
        //connect.getAccounts('d');
        //connect.getAccounts('s');
        //connect.getAccounts('l');

        //11. annulering af overførsel, tager imod et transaktions-id
        // og laver en tilsvarende overførsel mellem de to konti involveret (Linje 269)
        //connect.rollBackTransfer(56089);

        //12-13. Søgning på kunder fra en eller flere bestemte byer. Den metode kan tage imod et vilkårligt antal byer (Linje 293)
        //connect.searchCities("Frederikssund");
        //connect.searchCities("Frederikssund", "Frederiksberg", "Roskilde");

        //14. Valutakonvertering, denne metode udskriver en konto og dens balance i euro, den ændrer ikke i databasen (Linje 333)
        //connect.convertToEuro(1);

        //15. Den russiske oligark. Jeg har oprettet en oligark med en fiktiv adresse, og denne metode finder "oligarken"
        //Som det kan ses matcher postnummeret ikke med byen. Dette vil man i virkeligheden nok bruge machine learning til,
        // men det kunne være den type uoverensstemmelser man kunne kigge efter (Linje 363)
        //connect.findRussianOligarch();
    }

}
