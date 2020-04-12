public class HelloServiceImpl implements HelloService {
    public String sayHello(String name) {
        return "Hello " + name;
    }

    public int cal(int a, int b) {
        return a + b;
    }
}
