package simpledb;

import java.text.ParseException;
import java.util.*;
import java.io.*;

/**
 * A {@code HeapPage} is a collection of {@code Tuple}s. A fixed-size memory space is used for each {@code HeapPage}. A
 * {@code HeapFile} consists of {@code HeaPage}s. The {@code HeapPage} class implements the {@code Page} interface that
 * is used by {@code BufferPool}.
 * 
 * @see HeapFile
 * @see BufferPool
 */
public class HeapPage implements Page {

	/**
	 * The ID of this {@code HeapPage}.
	 */
	HeapPageId pid;

	/**
	 * The {@code TupleDesc} representing the {@code Tuple}s stored in this {@code HeapPage}.
	 */
	TupleDesc td;

	/**
	 * The collection storing the {@code Tuple}s assigned to this {@code HeapPage}.
	 */
	ArrayList<Tuple> tuples = new ArrayList<Tuple>();

	/**
	 * The previous image of this {@code HeapPage}.
	 */
	byte[] oldData;

	/**
	 * Creates a {@code HeapPage} from a byte array storing data read from disk. This byte array contains (1) a 4-byte
	 * integer representing the number of {@code Tuple}s assigned to the {@code HeapPage}, (2) a sequence of integer
	 * values each of which indicates where the corresponding {@code Tuple} is stored in the byte array, (3) a free
	 * space reserved for storing additional {@code Tuple}s, and (4) a sequence of {@code Tuple}s.
	 * 
	 * @param id
	 *            the ID of the {@code HeapPage}.
	 * @param data
	 *            a byte array storing the data to read.
	 * @see Database#getCatalog
	 * @see Catalog#getTupleDesc
	 * @see BufferPool#PAGE_SIZE
	 */
	public HeapPage(HeapPageId id, byte[] data) throws IOException {
		this.pid = id;
		this.td = Database.getCatalog().getTupleDesc(id.getTableId());
		int n = readInt(data, 0);
		for (int i = 0; i < n; i++)
			tuples.add(readTuple(data, i));
		setBeforeImage();
	}

	/**
	 * Reads a {@code Tuple} from the specified byte array.
	 * 
	 * @param data
	 *            a byte array storing the data to read.
	 * @param slotID
	 *            the ID of the slot at which the {@code Tuple} is stored.
	 * @throws NoSuchElementException
	 *             if there is no {@code Tuple} at the specified slot.
	 */
	protected Tuple readTuple(byte[] data, int slotId) throws NoSuchElementException {
		// some code goes here
		
		Tuple object;
		if(readInt(data, 0 )>slotId)
		{
			int off=readInt(data,4+(4*slotId));
			if(off!=-1)
			{
				try
				{
					DataInputStream in = new DataInputStream(new ByteArrayInputStream(data,off,data.length-off));
						object=readTuple(in, slotId);
						return object;
							
				}
				catch(ParseException e)
				{
				return null;
				}
			}
			else 	
				throw new NoSuchElementException("Offset is empty");
			
		}
		else 
			throw new NoSuchElementException("No slot");
		
		// throw new UnsupportedOperationException("Implement this");
	}

	/**
	 * Generates a byte array representing the contents of this {@code HeapPage}. This method is used to serialize this
	 * {@code HeapPage} to disk.
	 * <p>
	 * The invariant here is that it should be possible to pass the byte array generated by this method to the
	 * {@code HeapPage} constructor and have it produce an identical {@code HeapPage}.
	 * 
	 * @see #HeapPage
	 * @return a byte array correspond to the content of this {@code HeapPage}.
	 */
	public byte[] getPageData() {
		// some code goes here
		
		byte[] pd = new byte [BufferPool.PAGE_SIZE];
		writeInt(pd, 0 , this.tuples.size());
		int po= pd.length;
		for(int j=0;j<tuples.size();j++)
		{
			byte[] byteArray=toByteArray(tuples.get(j));
			int co=po-byteArray.length;		
			System.arraycopy(byteArray, 0 , pd , co , byteArray.length);
			writeInt(pd, 4*4+j , co);
			po=co;
		}
		return pd;
		
		// throw new UnsupportedOperationException("Implement this");
	}

	/**
	 * @return an iterator over all {@code Tuple}s on this {@code HeapPage} (calling remove on this iterator throws an
	 *         {@code UnsupportedOperationException}) (note that this iterator shouldn't return {@code Tuples} in empty
	 *         slots!)
	 */
	public Iterator<Tuple> iterator() {
		// some code goes here
		Iterator <Tuple> ti;
		ArrayList<Tuple> tl= new ArrayList<Tuple>();
		int nosTuple=readInt(this.getPageData(), 0 );
		for (int j=0;j<nosTuple;j++)
		{
			if(this.getTuple(j)!=null)
				tl.add(this.getTuple(j));
		}
		ti=tl.iterator();
		return ti;
		
		// 	throw new UnsupportedOperationException("Implement this");
	}

	/**
	 * @return the ID of this {@code HeapPage}.
	 */
	public HeapPageId getId() {
		return pid;
	}

	/**
	 * Deletes the specified {@code Tuple} from this {@code HeapPage}; the {@code Tuple} should be updated to reflect
	 * that it is no longer stored on any page.
	 * 
	 * @throws DbException
	 *             if the {@code Tuple} is not on this {@code HeapPage}, or the {@code Tuple} slot is already empty.
	 * @param t
	 *            the {@code Tuple} to delete
	 */
	public void deleteTuple(Tuple t) throws DbException {
		// some code goes here
		// not necessary for assignment1
	}

	/**
	 * Adds the specified {@code Tuple} to this {@code HeapPage}; the {@code Tuple} should be updated to reflect that it
	 * is now stored on this {@code HeapPage}.
	 * 
	 * @throws DbException
	 *             if this {@code HeapPage} is full (no empty slots).
	 * @param t
	 *            the {@code Tuple} to add.
	 */
	public void addTuple(Tuple t) throws DbException {
		// some code goes here
		// not necessary for assignment1
	}

	/**
	 * Marks this {@code HeapPage} as dirty/not dirty and record that transaction that did the dirtying
	 */
	public void markDirty(boolean dirty, TransactionId tid) {
		// some code goes here
		// not necessary for assignment1
	}

	/**
	 * Returns the tid of the transaction that last dirtied this page, or null if the page is not dirty
	 */
	public TransactionId isDirty() {
		// some code goes here
		// Not necessary for assignment1
		return null;
	}

	/**
	 * Returns {@code Tuple} at the specified slot.
	 * 
	 * @param slotId
	 *            the ID of the slot at which the {@code Tuple} is stored.
	 */
	public Tuple getTuple(int slotId) {
		if (slotId < 0 || slotId >= tuples.size())
			return null;
		else
			return tuples.get(slotId);
	}

	/**
	 * Returns a byte array representing the specified tuple.
	 * 
	 * @param t
	 *            a tuple.
	 * @return a byte array representing the specified tuple.
	 */
	protected byte[] toByteArray(Tuple t) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			for (int j = 0; j < t.fields.length; j++) {
				Field f = t.getField(j);
				f.serialize(out);
			}
			out.flush();
			b.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b.toByteArray();
	}

	/**
	 * Writes an integer value at the specified location of a byte array.
	 * 
	 * @param data
	 *            a byte array.
	 * @param offset
	 *            a location in the byte array.
	 * @param value
	 *            the value to write.
	 */
	protected void writeInt(byte[] data, int offset, int value) {
		data[offset] = (byte) (value >>> 24);
		data[offset + 1] = (byte) (value >>> 16);
		data[offset + 2] = (byte) (value >>> 8);
		data[offset + 3] = (byte) value;
	}

	/**
	 * Reads an integer at the specified location of a byte array.
	 * 
	 * @param data
	 *            a byte array.
	 * @param offset
	 *            a location in the byte array.
	 * @return an integer read at the specified location of a byte array.
	 */
	protected int readInt(byte[] data, int offset) {
		return ((data[offset]) << 24) + ((data[offset + 1] & 0xFF) << 16) + ((data[offset + 2] & 0xFF) << 8)
				+ (data[offset + 3] & 0xFF);
	}

	/**
	 * Reads a {@code Tuple} from the specified {@code DataInputStream}.
	 * 
	 * @param in
	 *            a {@code DataInputStream} storing the data to read.
	 * @param slotID
	 *            the ID of the slot at which the {@code Tuple} is stored.
	 * @return a {@code Tuple} constructed from the specified {@code DataInputStream}.
	 * @throws NoSuchElementException
	 *             if there is no {@code Tuple} at the specified slot.
	 * @throws ParseException
	 *             if there is a parsing error.
	 */
	protected Tuple readTuple(DataInputStream in, int slotId) throws NoSuchElementException, ParseException {
		Tuple t = new Tuple(td);
		RecordId rid = new RecordId(pid, slotId);
		t.setRecordId(rid);
		for (int j = 0; j < td.numFields(); j++) {
			Field f = td.getType(j).parse(in);
			t.setField(j, f);
		}
		return t;
	}

	/**
	 * Returns a view of this {@code HeapPage} before it was modified -- used by recovery
	 */
	public HeapPage getBeforeImage() {
		try {
			return new HeapPage(pid, oldData);
		} catch (IOException e) {
			e.printStackTrace();
			// should never happen -- we parsed it OK before!
			System.exit(1);
		}
		return null;
	}

	public void setBeforeImage() {
		oldData = getPageData().clone();
	}

	/**
	 * Generates a byte array corresponding to an empty {@code HeapPage}. This method is used to add new, empty pages to
	 * the file. Passing the results of this method to the {@code HeapPage} constructor will create a {@code HeapPage}
	 * with no valid {@code Tuple}s in it.
	 * 
	 * @return the created byte array.
	 */
	public static byte[] createEmptyPageData() {
		int len = BufferPool.PAGE_SIZE;
		return new byte[len]; // all 0
	}

}