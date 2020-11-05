public class Main {
    public static void main(String[] args) {
        String fileName = null;
        String regex = null;
        if (args.length >= 2) {
            String firstOption = args[0];
            if (firstOption.equals("-f")) {
                fileName = args[1];
            }
        }

        if (args.length >= 4) {
            String secondOption = args[2];
            if (secondOption.equals("-s")) {
                regex = args[3];
            }
        }

        FileNodesFinder finder = new FileNodesFinder();
        finder.find(fileName, regex);
    }
}

