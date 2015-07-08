package com.dushyant.services;


/**
 * i know, i am supposed to unit test...
 *
 */
public class Test {
  class Name {
    String a;
    String b;
    String c;

    public Name(String a, String b, String c) {
      super();
      this.a = a;
      this.b = b;
      this.c = c;
    }

    public String getA() {
      return a;
    }

    public void setA(String a) {
      this.a = a;
    }

    public String getB() {
      return b;
    }

    public void setB(String b) {
      this.b = b;
    }

    public String getC() {
      return c;
    }

    public void setC(String c) {
      this.c = c;
    }

    @Override
    public String toString() {
      return String.format("Name [a=%s, b=%s, c=%s]", a, b, c);
    }


  }

  public static void main(String[] args) throws Exception {
    Test test = new Test();
    System.out.println(Method.<Boolean>execute(test, "b", Boolean.class));
    System.out.println(Method.<Boolean>execute(test, "b1", Boolean.class));
    System.out.println(Method.execute(test, "getName", "dushyant", String.class));
    System.out
        .println(Method.<Name>execute(test, "getName", "dushyadnt", String.class, Name.class));
    Name execute =
        Method.<Name>execute(test, "getName", new Object[] {"a", false}, new Class<?>[] {
            String.class, Boolean.class}, Name.class);
    System.out.println(execute);
  }

  public void close() {
    System.out.println("close");
  }

  public void close(String a) {
    System.out.println("close a" + a);
  }

  public boolean b() {
    return false;
  }

  public boolean b1() throws Exception {
    return true;
  }

  public Name getName(String name) {
    return new Name(name, name, name);
  }

  public Name getName(String name, Boolean boolean1) {
    return new Name(name, name, name);
  }
}

