public class Generator {

    public static String generateAdress(){
        String[] name = {"Store", "Jensens", "Park", "Vestre", "Nordre", "Valby", "Hansens", "Nielsens", "Stadion"};
        String[] gade = {"vej ", "gade ", " Boulevard ", " Allé "};
        int rand = (int) (Math.random() * 100 + 1);
        String nummer = Integer.toString(rand);

        rand = (int) (Math.random() * 40);
        return name[rand % 9] + gade[rand % 4] + nummer;
    }

    public static String generatePostalCode(int rand){
        String[] codes = {"4000", "2000", "2600", "3600"};
        return codes[rand];
    }

    public static String generateCity(int rand){
        String[] citys = {"Roskilde", " Frederiksberg", "Glostrup", "Frederikssund"};
        return citys[rand];
    }

    public static String generateName(){
        String[] firstName = {"Sune", "Simon", "Hans", "Jens", "Anders", "Niels", "Thomas",
                "Tine", "Line", "Hanne", "Sanne", "Birthe", "Charlotte", "Henriette"};
        String[] surName = {"Jensen", "Hansen", "Nielsen", "Thomsen", "Tobiasen", "Mortensen", "Andersen", "Svendsen", "Kristensen"};

        int rand = (int) (Math.random() * 125);
        return firstName[rand % 14] + " " + surName[rand % 9];
    }
}
