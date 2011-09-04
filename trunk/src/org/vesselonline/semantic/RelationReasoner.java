package org.vesselonline.semantic;

public class RelationReasoner {
  // Is x a sibling of y?  True if either person is a sibling of the other.
  public static boolean isSiblingOf(Person x, Person y) {
    return (x.getSiblings().contains(y) || y.getSiblings().contains(x)) ? true : false;
  }

  // Is x a child of y?  True if x is a child of y or y is a parent of x.
  public static boolean isChildOf(Person x, Person y) {
    return (x.getParents().contains(y) || y.getChildren().contains(x)) ? true : false;
  }

  // Is x a parent of y?  True if isChildOf(y, x) is true.
  public static boolean isParentOf(Person x, Person y) {
    return (isChildOf(y, x)) ? true : false;
  }

  // Is x an uncle of y?  See inline comments for description.
  public static boolean isUncleOf(Person x, Person y) {
    // If x is not a male, then return false.
    if (! x.getGender().equals(Person.MALE)) return false;

    // Iterate through y's parents to build a list of all of their siblings.
    // If x is in the list of siblings, then he is an uncle of y.
    for (Person parent : y.getParents()) {
System.out.println("Child's Parent: " + parent.toString());
      for (Person sibling : parent.getSiblings()) {
System.out.println("  Parent's Sibling: " + sibling.toString());
        if (sibling.equals(x)) return true;
      }
    }

    // Iterate through x's siblings to build a list of all of their children.
    // If y is in the list of children, then he/she is a nephew/niece of x.
    for (Person sibling : x.getSiblings()) {
System.out.println("Uncle's Sibling: " + sibling.toString());
      for (Person child : sibling.getChildren()) {
System.out.println("  Sibling's Child: " + child.toString());
        if (child.equals(y)) return true;
      }
    }    

    return false;
  }

  public static void main(String[] args) {
    Person a = new Person("Mom", Person.FEMALE);
    Person b = new Person("Uncle", Person.MALE);
    Person c = new Person("Kid", Person.MALE);

    a.addRelation(new Relation(c, Relation.CHILD));
    b.addRelation(new Relation(a, Relation.SIBLING));

    System.out.println("Person a:  " + a.toString());
    for (Relation relation : a.getRelations()) {
      System.out.println("  " + relation.toString());
    }
    System.out.println();

    System.out.println("Person b:  " + b.toString());
    for (Relation relation : b.getRelations()) {
      System.out.println("  " + relation.toString());
    }
    System.out.println();

    System.out.println("Person c:  " + c.toString() + "\n");
/* Uncomment the following lines to test basic functionality.
    System.out.println("isSiblingOf(a, b): " + isSiblingOf(a, b));
    System.out.println("isSiblingOf(b, a): " + isSiblingOf(b, a));
    System.out.println("isSiblingOf(b, c): " + isSiblingOf(b, c));
    System.out.println();
    System.out.println("isChildOf(c, a): " + isChildOf(c, a));
    System.out.println("isChildOf(a, b): " + isChildOf(a, b));
    System.out.println("isChildOf(a, c): " + isChildOf(a, c));
    System.out.println();
    System.out.println("isParentOf(a, c): " + isParentOf(a, c));
    System.out.println("isParentOf(c, a): " + isParentOf(c, a));
    System.out.println("isParentOf(b, c): " + isParentOf(b, c));
    System.out.println();
*/
    System.out.println("isUncleOf(a, c): " + isUncleOf(a, c));  // Should be false
    System.out.println("isUncleOf(a, b): " + isUncleOf(a, c));  // Should be false
    System.out.println("");
    System.out.println("isUncleOf(b, c): " + isUncleOf(b, c));  // Should be true
  }
}
