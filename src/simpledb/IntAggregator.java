package simpledb;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntAggregator implements Aggregator {

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
	 *            the aggregation operator
	 */

	HashMap<Field, Integer> groups;
	HashMap<Field, Integer> counts;
	String fieldName = "";
	String groupFieldName = "";
	int GBField;
	Type GBFieldType;
	int AField;
	Op What;
	boolean noGrouping = false;// set to true for grouping enabled

	public IntAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
		// some code goes here
		GBField = gbfield;
		GBFieldType = gbfieldtype;
		AField = afield;
		What = what;
		groups = new HashMap<Field, Integer>();
		counts = new HashMap<Field, Integer>();
		if (GBField == Aggregator.NO_GROUPING) {
			noGrouping = true;
		}
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
		int curaggrval;
		int count;
		fieldName = tup.getTupleDesc().getFieldName(AField);
		if (noGrouping) {
			field = new IntField(Aggregator.NO_GROUPING);
		} else {
			field = tup.getField(GBField);
			groupFieldName = tup.getTupleDesc().getFieldName(GBField);

		}

		int value = ((IntField) tup.getField(AField)).getValue();

		if (counts.containsKey(field)) {
			count = counts.get(field); // get the count
		}
		if (groups.containsKey(field)) {
			curaggrval = groups.get(field);// get the aggr function
		} else {
			if (What == Op.COUNT || What == Op.SUM || What == Op.AVG) {
				groups.put(field, 0);
				counts.put(field, 0);
			}
			if (What == Op.MAX) {
				groups.put(field, Integer.MIN_VALUE);
				counts.put(field, 0);
			}
			if (What == Op.MIN) {
				groups.put(field, Integer.MAX_VALUE);
				counts.put(field, 0);
			}
		}
		curaggrval = groups.get(field);
		count = counts.get(field);

		if (What == Op.MIN) {
			if (value < curaggrval) {
				curaggrval = value;
				groups.put(field, curaggrval);
			}
		}

		if (What == Op.MAX) {
			if (value > curaggrval) {
				curaggrval = value;
				groups.put(field, curaggrval);
			}
		}

		if (What == Op.COUNT) {
			curaggrval++;
			groups.put(field, curaggrval);
		}

		if (What == Op.SUM) {
			curaggrval += value;
			groups.put(field, curaggrval);
		}

		if (What == Op.AVG) {
			count++;
			counts.put(field, count);
			curaggrval += value;
			groups.put(field, curaggrval);
		}

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
		TupleDesc td = new TupleDesc(type,str);
		int value;
		TupleIterator ti ;
		if (noGrouping) {
			for (Field field : groups.keySet()) {
				value = groups.get(field);
				if (What == Op.AVG) {
					value = value / counts.get(field);
				}
				Tuple tuple = new Tuple(td);
				tuple.setField(0, new IntField(value));
				tuples.add(tuple);
			}
		} else {
			for (Field key : groups.keySet()) {
				value = groups.get(key);
				if (What == Op.AVG) {
					value = value / counts.get(key);
				}

				Tuple tuple = new Tuple(td);
				tuple.setField(0, key);
				tuple.setField(1, new IntField(value));
				tuples.add(tuple);
			}
		}
		ti= new TupleIterator(td, tuples);
		return ti;
	}
	
	
}
