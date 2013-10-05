

class Utilities
{
  // This method can be used to wait until you're ready to proceed.
  public static void waitHere(String msg)
  {
    System.out.print("\n" + msg);
    try { System.in.read(); }
    catch(Exception e) {} // Ignore any errors while reading.
  }
}