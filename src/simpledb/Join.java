package simpledb;

import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends AbstractDbIterator {

	/**
	 * The TupleDesc for this Join
	 */
    TupleDesc td;

    /**
     * The predicate to use to join the children
     */
	private JoinPredicate p;
	
    /**
     * Iterator for the left(outer) relation to join
     */
	private DbIterator child1;
	
	/**
	 * Iterator for the right(inner) relation to join
	 */
	private DbIterator child2;

	/**
	 * Current tuple from the left(outer) relation to join
	 */
    Tuple t1 = null;

    /**
     * Constructor.  Accepts to children to join and the predicate
     * to join them on
     *
     * @param p The predicate to use to join the children
     * @param child1 Iterator for the left(outer) relation to join
     * @param child2 Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, DbIterator child1, DbIterator child2) {
    	this.child1 = child1;
    	this.child2 = child2;
    	this.p = p;
        td=TupleDesc.combine(child1.getTupleDesc(), child2.getTupleDesc());
     }

	/**
     * @see simpledb.TupleDesc#combine(TupleDesc, TupleDesc) for possible implementation logic.
     */
    public TupleDesc getTupleDesc() {
        return td;
    }

    public void open()
        throws DbException, NoSuchElementException, TransactionAbortedException {
        child1.open();
        child2.open();
    }

    public void close() {
        child1.close();
        child2.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        close();
        open();
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no more tuples.
     * Logically, this is the next tuple in r1 cross r2 that satisfies the join
     * predicate.  There are many possible implementations; the simplest is a
     * nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of
     * Join are simply the concatenation of joining tuples from the left and
     * right relation. Therefore, if an equality predicate is used 
     * there will be two copies of the join attribute
     * in the results.  (Removing such duplicate columns can be done with an
     * additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     *
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple readNext() throws TransactionAbortedException, DbException {
    	if (t1 == null) {
    		if (child1.hasNext())
    			t1 = child1.next();
    		else
    			return null;
    	}
    	while (true) {
            while (child2.hasNext()) {
                Tuple t2 = child2.next();
                if(p.filter(t1, t2)){
                	return concatenate(t1, t2);
                }
            }
            child2.rewind();
            if (child1.hasNext())
            	t1 = child1.next();
            else 
            	return null;
    	}
    }

    /**
     * Returns the concatenation of the specified tuples.
     * @param t1 a Tuple
     * @param t2 a Tuple
     * @return the concatenation of the specified tuples.
     */
    protected Tuple concatenate(Tuple t1, Tuple t2) {
        Tuple o = new Tuple(td);
        for(int i=0;i<t1.getTupleDesc().numFields();i++)
            o.setField(i, t1.getField(i));
        for(int j=0;j<t2.getTupleDesc().numFields();j++)
            o.setField(j+t1.getTupleDesc().numFields(), t2.getField(j));
        return o;
	}

}