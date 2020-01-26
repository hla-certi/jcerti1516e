package hla.rti1516e.jlc;


import java.util.HashMap;

import java.util.Map;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;


/**
 * HLAvariantRecordImpl is the implementation of type HLAvariantRecord
 * It is a map witch associate a discriminant with the alternative
 * All the discriminant have the same type T, witch can be a simpple or a complexe type
 * The alternatives can have each a different type, simple or complexe 
 * @param <T> Type of the discriminants. Can be a simple type or a complexe type
 */

public class HLAvariantRecordImpl<T extends DataElement> extends DataElementBase 
	implements hla.rti1516e.encoding.HLAvariantRecord<T> {

	private Map<T, DataElement> variants = new HashMap<T, DataElement>();;
	private T discriminant;
	
	/**
	 * Empty contructor of HLAvariantRecordImpl
	 */
	public HLAvariantRecordImpl() {
	}
	
	/**
	 * Constructor of HLAvariantRecordImpl with a discriminant T
	 * Change the value of current discriminent but does'nt add anything to the map
	 * @param discriminant : Discriminant of type T. All the other discriminant will have to be of the same type.
	 */
	public HLAvariantRecordImpl(T discriminant) {
		this.discriminant = discriminant;
	}
	
	/**
	 * Constructor of HLAvariantRecordImpl with a discriminant T
	 * Change the value of current discriminent and add the couple (discriminant, alternative) to the map
	 * @param discriminant : Discriminant of type T. All the other discriminant will have to be of the same type.
	 * @param dataElement : Alternative to associate with the discriminant. 
	 */
	public HLAvariantRecordImpl(T discriminant, DataElement dataElement) {
		this.variants.put(discriminant, dataElement);
		this.discriminant = discriminant;
	}
	
	/**
	 * Calcul the octet boundary 
	 * HLAVariantRecord octet boundary is the max of discriminant's octet boundary and each alternative
	 * 
	 * @return Value of octet boundary
	 */
	@Override
	public int getOctetBoundary() {
		if(this.discriminant == null) return 1;
		
		int maxOctetBoundary = 1;
		for(DataElement v : this.variants.values()) {
			maxOctetBoundary = Math.max(maxOctetBoundary, v.getOctetBoundary());
        }
		return maxOctetBoundary;
	}
	
	/**
	 * Calcul the length necessary to encode this HLAVariantRecord
	 * The lenght is the sum of the discriminent lenght, its alternative and the padding
	 * HLAVariantRecord octet boundary is the max of discriminant's octet boundary and each alternative
	 * 
	 * @return Lenght necessary to encode the variant
	 */
	@Override
	public int getEncodedLength() {
		
		if(this.discriminant == null) return 0;
		if(this.getValue() == null) return 0;
		
		int elength = this.discriminant.getEncodedLength();
		
        if(this.getValue() != null) {
        	elength += this.getValue().getEncodedLength();
        	elength += this.calculPaddingAfterDiscriminant(this.discriminant.getEncodedLength(), this.getOctetBoundary());
        }
        return elength;
	}

	/**
	 * Encode the VariantRecord
	 * @param Object byteWrapper, initialized with the correct lenght
	 */
	@Override
	public void encode(ByteWrapper byteWrapper) throws EncoderException {
		if(this.discriminant == null) throw new EncoderException("HLAvariantRecord discriminent is null");
		if(!this.variants.containsKey(discriminant)) throw new EncoderException("HLAvariantRecord discriminent doesn't have value affected");
		
		byteWrapper.align(getOctetBoundary());
		this.discriminant.encode(byteWrapper);
		
		int paddingSize = calculPaddingAfterDiscriminant(this.discriminant.getEncodedLength(), this.getOctetBoundary());
		if(paddingSize > 0) {
				byte[] paddingBytes = new byte[paddingSize];
				for(int i =0; i<paddingSize; i++) paddingBytes[i] = 0;
				byteWrapper.put(paddingBytes);
		}
		this.getValue().encode(byteWrapper);
	}
	
	/**
	 * Calcul the size of the padding necesary
	 * @param size Discriminent : encoded lenght of the discriminent
	 * @param octetBoundary : octet boundary of the VariantRecord
	 * @return size of the padding
	 */
	private int calculPaddingAfterDiscriminant(int sizediscriminant, int octetboundary) {
		int p = 0;
		int r = sizediscriminant % octetboundary;
		if(r == 0) p = 0;
		else p = octetboundary -  r;
		return p;
	}

	/**
	 * Decode the VariantRecord
	 * The structure of the variant must be defined before decoding it
	 * @param Object byteWrapper, initialized with the correct lenght, with position reset
	 */
	@Override
	public void decode(ByteWrapper byteWrapper) throws DecoderException {
		byteWrapper.align(this.getOctetBoundary());
		
		discriminant.decode(byteWrapper); 
		
		int padding;
		if(this.getValue() == null) {
			padding = this.getOctetBoundary();
		} else {
			padding = calculPaddingAfterDiscriminant(this.discriminant.getEncodedLength(), this.getOctetBoundary());
		}
		if(padding != 0) byteWrapper.advance(padding);
		this.getValue().decode(byteWrapper);
	}

	/**
	 * Add a new pair of a discriminent and this alternative in the variant
	 * Also change the value of the current discriminent
	 * @param Discriminant 
	 * @param dataElement : alternative to asociate with the discriminant
	 */
	@Override
	public void setVariant(T discriminant, DataElement dataElement){
		variants.put(discriminant, dataElement);
		this.discriminant = discriminant;
	}

	/**
	 * Choose a value in the map by the discriminant
	 * @param Discriminent to set to the current discriminant
	 */
	@Override
	public void setDiscriminant(T discriminant) {
		this.discriminant = discriminant;
	}

	/**
	 * Get the current discriminant
	 * @return Current discriminent
	 */
	@Override
	public T getDiscriminant() {
		return this.discriminant;
	}

	/**
	 * Get the value associated to the current discriminent
	 * @return value associated to the current discriminent
	 */
	@Override
	public DataElement getValue() {
		if(this.discriminant == null) return null;
		if(!variants.containsKey(this.discriminant)) return null;
		return this.variants.get(discriminant);
	}
	
}

