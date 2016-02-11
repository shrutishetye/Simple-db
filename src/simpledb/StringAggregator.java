package simpledb;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

	HashMap<Field, Integer> groups;
	HashMap<Field, Integer> counts;
	String fieldName = "";
	String groupFieldName = "";
	int GBField;
	Type GBFieldType;
	int AField;
	Op What;
	boolean noGrouping = false;// set to true for grouping enabled

	/**
	 * Aggregate constructor
	 * 
	 * @param gbfield
	 *            the 0-based index of the group-by field in the tuple, or
	 *            NO_GROUPING if there is no grouping
	 * @param gbfieldtype
	 *            the type of the group by field (e.g., Type.INT_TYPE), or null
	 *            if there is no grouping
	 * @param afield
	 *            the 0-based index of the aggregate field in the tuple
	 * @param what
	 *            aggregation operator to use -- only supports COUNT
	 * @throws IllegalArgumentException
	 *             if what != COUNT
	 */

	public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
		if (gbfield == Aggregator.NO_GROUPING) {
			noGrouping = true;
		}
		GBField = gbfield;
		GBFieldType = gbfieldtype;
		AField = afield;
		What = what;
		groups = new HashMap<Field, Integer>();
	}

	/**
	 * Merge a new tuple into the aggregate, grouping as indicated in the
	 * constructor
	 * 
	 * @param tup
	 *            the Tuple containing an aggregate field and a group-by field
	 */
	public void merge(Tuple tup) {
		// some code goes here
		Field field;
		int curraggrval;
		fieldName = tup.getTupleDesc().getFieldName(AField);
		if (noGrouping) {
			field = new IntField(Aggregator.NO_GROUPING);
		} else {
			field = tup.getField(GBField);
			groupFieldName = tup.getTupleDesc().getFieldName(GBField);

		}

		if (groups.containsKey(field)) {
			curraggrval = groups.get(field);
		} else {
			groups.put(field, 0);
		}
		curraggrval = groups.get(field);
		curraggrval++;
		groups.put(field, curraggrval);
	}

	/**
	 * Create a DbIterator over group aggregate results.
	 *
	 * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
	 *         if using group, or a single (aggregateVal) if no grouping. The
	 *         aggregateVal is determined by the type of aggregate specified in
	 *         the constructor.
	 */
	public DbIterator iterator() {
		// some code goes here
		// throw new UnsupportedOperationException("implement me");
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		Type[] type;
		String[] str;
		if (noGrouping) {
			type = new Type[1];
			str = new String[1];
			type[0] = Type.INT_TYPE;
		} else {
			type = new Type[2];
			str = new String[2];
			type[0] = GBFieldType;
			type[1] = Type.INT_TYPE;
			str[0] = groupFieldName;
			str[1] = fieldName;
		}
		TupleDesc td = new TupleDesc(type, str);

		if (noGrouping) {
			for (Field key : groups.keySet()) {
				int value = groups.get(key);

				Tuple tuple = new Tuple(td);
				tuple.setField(0, new IntField(value));
				tuples.add(tuple);
			}
		} else {
			for (Field key : groups.keySet()) {
				int value = groups.get(key);

				Tuple tuple = new Tuple(td);
				tuple.setField(0, key);
				tuple.setField(1, new IntField(value));
				tuples.add(tuple);
			}
		}
		return new TupleIterator(td, tuples);
	}
}
