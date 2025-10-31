public class Main {

    public static void main(String[] _args) {

        SSLClient agent = new SSLClient("10.8.0.1", 4520);
        agent.connect();
    }
}