
/**
 * Represents a single binary feature with two String values.
 */
class Feature 
{
  private String name;
  private String firstValue;
  private String secondValue;

  public Feature(String name, String first, String second) 
  {
    this.name = name;
    firstValue = first;
    secondValue = second;
  }

  public String getName()
  {
    return name;
  }

  public String getFirstValue()
  {
    return firstValue;
  }

  public String getSecondValue() 
  {
    return secondValue;
  }
}