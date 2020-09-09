
package demos.dlineage.util;

/**
 * Container to ease passing around a tuple of two objects. This object provides
 * a sensible implementation of equals(), returning true if equals() is true on
 * each of the contained objects.
 */
public class Pair3<F, S, T> {

    public final F first;
    public final S second;
    public final T third;

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public Pair3(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link Pair3} to which this one is to be checked for
     *          equality
     * @return true if the underlying objects of the Pair are both considered
     * equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair3)) {
            return false;
        }
        Pair3<?, ?, ?> p = (Pair3<?, ?, ?>) o;
        return Objects.equal(p.first, first)
                && Objects.equal(p.second, second)
                && Objects.equal(p.third, third);
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode() {
        return ((first == null ? 0 : first.hashCode())
                ^ (second == null ? 0 : second.hashCode())) 
        		^ (third == null ? 0 : third.hashCode());
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     *
     * @param a the first object in the Pair
     * @param b the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    public static <A, B, C> Pair3<A, B, C> create(A a, B b, C c) {
        return new Pair3<A, B, C>(a, b, c);
    }

    public String toString() {
    	if(third!=null){
    		return "[" + first + "," + second + "," + third + "]";
    	}
    	else{
    		return "[" + first + "," + second + "]";
    	}
    }
}