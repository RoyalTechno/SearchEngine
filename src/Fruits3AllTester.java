public class Fruits3AllTester {
    public static void main(String[] args) throws Exception {
        ProjectTester tester = new ProjectTesterImp(); //Instantiate your own ProjectTester instance here
        double time = System.nanoTime();
        tester.initialize();
        tester.crawl("https://people.scs.carleton.ca/~davidmckenney/fruits3/N-0.html");

        Fruits3OutgoingLinksTester.runTest(tester);
        Fruits3IncomingLinksTester.runTest(tester);
        Fruits3PageRanksTester.runTest(tester);
        Fruits3IDFTester.runTest(tester);
        Fruits3TFTester.runTest(tester);
        Fruits3TFIDFTester.runTest(tester);
        Fruits3SearchTester.runTest(tester);
        System.out.println((System.nanoTime()-time)/1000000000);
        System.out.println("Finished running all tests.");
    }
}
