package bsh;

/**
 * An interface implemented by classes wrapping instances of iterators, enumerations, collections,
 * etc.
 *
 * @see CollectionManager.getBshIterator(Object)
 */
public interface BshIterator {
    /**
     * Fetch the next object in the iteration
     *
     * @return The next object
     */
    public Object next();

    /**
     * Returns true if and only if there are more objects available via the <code>next()</code>
     * method
     *
     * @return The next object
     */
    public boolean hasNext();
}
