package de.persosim.editor.ui.editor.signing;

import static org.globaltester.logging.BasicLogger.logException;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collection;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.crypto.SignatureOids;
import de.persosim.simulator.crypto.Tr03111;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvTagIdentifier;

/**
 * Abstract implementation of {@link SecInfoCmsBuilder}, which provides common
 * implementations for building a SignedData structure.
 * <p/>
 * A very simple implementation is provided. For example it does not account for
 * CRLs or multiple signers/digestAlgorithms etc. If this kind of flexibility is
 * needed the interface {@link SecInfoCmsBuilder} needs to be implemented
 * independently.
 *
 * @author amay, cstroh
 *
 */
public class SecInfoCmsBuilder implements TlvConstants {

	private final Oid OID_SHA1 = new GenericOid(new byte[] { 0x2B, 0x0E, 0x03, 0x02, 0x1A });
	private final Oid OID_SHA224 = new GenericOid(
			new byte[] { 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x04 });
	private final Oid OID_SHA256 = new GenericOid(
			new byte[] { 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01 });
	private final Oid OID_SHA384 = new GenericOid(
			new byte[] { 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x02 });
	private final Oid OID_SHA512 = new GenericOid(
			new byte[] { 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x03 });
	private final Oid OID_messageDigest = new GenericOid(
			new byte[] { 0x2A, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xF7, 0x0D, 0x01, 0x09, 0x04 });
	private final Oid OID_contentType = new GenericOid(
			new byte[] { 0x2A, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xF7, 0x0D, 0x01, 0x09, 0x03 });

	private final PrimitiveTlvDataObject noParameters = new PrimitiveTlvDataObject(TAG_NULL);
	public final Oid OID_BSI_SECURITY_OBJECT = new GenericOid(new byte[]{0x04, 0x00, 0x7F, 0x00, 0x07, 0x03, 0x02, 0x01});
	public final Oid OID_ICAO_SECURITY_OBJECT = new GenericOid(new byte[]{0x67, (byte) 0x81, 0x08, 0x01, 0x01, 0x01});

	private Oid encapContentInfoOid = OID_BSI_SECURITY_OBJECT;
	private byte[] dscert;
	private PrivateKey dsPrivKey;

	/**
	 * @param dscert
	 *            The DS certificate to use for building the CMS SignedData
	 *            structure
	 * @param dsPrivKey
	 *            The corresponding private key of the DS certificate
	 * @return
	 * @throws InvalidKeySpecException
	 */
	public SecInfoCmsBuilder(byte[] dscert, byte[] dsPrivKey) throws InvalidKeySpecException {
		this.dscert = dscert;
		this.dsPrivKey = getKeyFromByteArray(dsPrivKey);
	}

	public ConstructedTlvDataObject buildSignedData(ConstructedTlvDataObject secInfos)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		// version defaults to 3 in this implementation
		TlvDataObject version = new PrimitiveTlvDataObject(new TlvTag(Asn1.INTEGER), new byte[] { 0x03 });

		// digestAlgorithms
		ConstructedTlvDataObject digestAlgorithms = new ConstructedTlvDataObject(TAG_SET);
		digestAlgorithms.addAll(getDigestAlgorithms());

		// encapContentInfo
		ConstructedTlvDataObject encapContentInfo = getEncapContentInfo(secInfos);

		// certificates
		ConstructedTlvDataObject certificates = new ConstructedTlvDataObject(TAG_A0);
		certificates.addAll(Arrays.asList(getCertificate()));

		// signerInfos
		ConstructedTlvDataObject signerInfos = new ConstructedTlvDataObject(TAG_SET);
		signerInfos.addAll(getSignerInfos(encapContentInfo));

		// signedData
		ConstructedTlvDataObject signedData = new ConstructedTlvDataObject(TAG_SEQUENCE);
		signedData.addTlvDataObject(version);
		signedData.addTlvDataObject(digestAlgorithms);
		signedData.addTlvDataObject(encapContentInfo);
		signedData.addTlvDataObject(certificates);
		signedData.addTlvDataObject(signerInfos);

		return signedData;
	}

	/**
	 * Build the encapContantInfo from the provided SecInfos
	 *
	 * @param secInfos
	 * @return
	 */
	protected ConstructedTlvDataObject getEncapContentInfo(ConstructedTlvDataObject secInfos) {
		TlvDataObject contentType = new PrimitiveTlvDataObject(TAG_OID, encapContentInfoOid.toByteArray());
		ConstructedTlvDataObject eContent = new ConstructedTlvDataObject(TAG_A0);
		eContent.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OCTET_STRING, secInfos.toByteArray()));
		ConstructedTlvDataObject encapContentInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		encapContentInfo.addTlvDataObject(contentType);
		encapContentInfo.addTlvDataObject(eContent);
		return encapContentInfo;
	}

	public void setEncapContentInfoOid(Oid encapContentInfoOid) {
		this.encapContentInfoOid = encapContentInfoOid;
	}

	/**
	 * Return all used digestAlgorithms, defaults to a Collection of the single
	 * digestAlgorithm returned by {@link #getDigestAlgorithm()}
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	protected Collection<? extends TlvDataObject> getDigestAlgorithms() throws NoSuchAlgorithmException {
		return Arrays.asList(getDigestAlgorithm());
	}

	/**
	 * Return the single used digestAlgorithm
	 * <p/>
	 * If more than one digestAlgorithm is used override
	 * {@link #getDigestAlgorithms()} and ignore this method
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	protected TlvDataObject getDigestAlgorithm() throws NoSuchAlgorithmException {
		String signatureAlg = getSignatureAlgorithmString();

		if (signatureAlg.startsWith("SHA1")) {
			return new ConstructedTlvDataObject(TAG_SEQUENCE,
					new PrimitiveTlvDataObject(TAG_OID, OID_SHA1.toByteArray()), noParameters);
		} else if (signatureAlg.startsWith("SHA224")) {
			return new ConstructedTlvDataObject(TAG_SEQUENCE,
					new PrimitiveTlvDataObject(TAG_OID, OID_SHA224.toByteArray()), noParameters);
		} else if (signatureAlg.startsWith("SHA256")) {
			return new ConstructedTlvDataObject(TAG_SEQUENCE,
					new PrimitiveTlvDataObject(TAG_OID, OID_SHA256.toByteArray()), noParameters);
		} else if (signatureAlg.startsWith("SHA384")) {
			return new ConstructedTlvDataObject(TAG_SEQUENCE,
					new PrimitiveTlvDataObject(TAG_OID, OID_SHA384.toByteArray()), noParameters);
		} else if (signatureAlg.startsWith("SHA512")) {
			return new ConstructedTlvDataObject(TAG_SEQUENCE,
					new PrimitiveTlvDataObject(TAG_OID, OID_SHA512.toByteArray()), noParameters);
		} else {
			throw new NoSuchAlgorithmException("Digest algorithm '" + signatureAlg + "' is not supported by Signer.");
		}
	}

	protected String getSignatureAlgorithmString() {
		Oid sigAlg = new GenericOid(getSignatureAlgorithm().getTlvDataObject(TAG_OID).getValueField());

		if (SignatureOids.id_sha1withrsaencryption.equals(sigAlg)) {
			return "SHA1withRSA";
		} else if (SignatureOids.id_sha256withrsaencryption.equals(sigAlg)) {
			return "SHA256withRSA";
		} else if (SignatureOids.id_rsassapss.equals(sigAlg)) {
			return "SHA256withRSA/PSS"; // FIXME: Possibly, this hardcoded SHA-RSA-PSS combination has to be changed to support other SHA algorithm (see GT implementation).
		} if (SignatureOids.id_ecdsawithSHA224.equals(sigAlg)) {
			return "SHA224withECDSA";
		} if (SignatureOids.id_ecdsawithSHA256.equals(sigAlg)) {
			return "SHA256withECDSA";
		} if (SignatureOids.id_ecdsawithSHA384.equals(sigAlg)) {
			return "SHA384withECDSA";
		} if (SignatureOids.id_ecdsawithSHA512.equals(sigAlg)) {
			return "SHA512withECDSA";
		} if (Tr03111.id_ecdsa_plain_SHA1.equals(sigAlg)) {
			return "SHA1withECDSA";
		} if (Tr03111.id_ecdsa_plain_SHA224.equals(sigAlg)) {
			return "SHA224withECDSA";
		} if (Tr03111.id_ecdsa_plain_SHA256.equals(sigAlg)) {
			return "SHA256withECDSA";
		} if (Tr03111.id_ecdsa_plain_SHA384.equals(sigAlg)) {
			return "SHA384withECDSA";
		} if (Tr03111.id_ecdsa_plain_SHA512.equals(sigAlg)) {
			return "SHA512withECDSA";
		}

		return null;
	}

	/**
	 * Return the used signature algorithm
	 *
	 * @return
	 */
	protected ConstructedTlvDataObject getSignatureAlgorithm() {
		ConstructedTlvDataObject dscertTLV = getCertificate();
		ConstructedTlvDataObject sigAlg = (ConstructedTlvDataObject) dscertTLV
				.getTlvDataObject(new TlvPath(TAG_SEQUENCE, TAG_SEQUENCE));
		return sigAlg;
	}

	/**
	 * Return all used certificates, defaults to a Collection of the single
	 * certificate returned by {@link #getCertificate()}
	 *
	 * @return
	 */
	protected ConstructedTlvDataObject getCertificate() {
		return new ConstructedTlvDataObject(dscert);
	}

	/**
	 * Return all used signerInfos, defaults to a Collection of the single
	 * signerInfo returned by {@link #getSignerInfo()}
	 *
	 * @param eContent
	 *            the encapContentInfo to be signed
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	protected Collection<? extends TlvDataObject> getSignerInfos(ConstructedTlvDataObject eContent)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		return Arrays.asList(getSignerInfo(eContent));
	}

	/**
	 * Return the single used SignerInfo
	 * <p/>
	 * If more than one SignerInfo is used override {@link #getSignerInfos()} and
	 * ignore this method
	 *
	 * @param eContent
	 *            the encapContentInfo to be signed
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	protected TlvDataObject getSignerInfo(ConstructedTlvDataObject eContent)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		// version defaults to 1 in this implementation
		TlvDataObject version = new PrimitiveTlvDataObject(new TlvTag(Asn1.INTEGER), new byte[] { 0x01 });

		// SignerIdentifier
		TlvDataObject sid = getSid();

		// digestAlgorithm
		TlvDataObject digestAlgorithm = getDigestAlgorithm();

		// signedAttrs
		TlvDataObject signedAttrs = getSignedAttrs(eContent);

		// signatureAlgorithm
		TlvDataObject signatureAlgorithm = getSignatureAlgorithm();

		// signature
		byte[] signatureBytes;
		if (signedAttrs != null) {
			byte[] sigInput = signedAttrs.toByteArray();
			sigInput[0] = Asn1.SET;
			signatureBytes = getSignature(sigInput);
		} else {
			signatureBytes = getSignature(eContent.toByteArray());
		}
		TlvDataObject signature = new PrimitiveTlvDataObject(TAG_OCTET_STRING, signatureBytes);

		ConstructedTlvDataObject signerInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		signerInfo.addTlvDataObject(version);
		signerInfo.addTlvDataObject(sid);
		signerInfo.addTlvDataObject(digestAlgorithm);
		signerInfo.addTlvDataObject(signedAttrs);
		signerInfo.addTlvDataObject(signatureAlgorithm);
		signerInfo.addTlvDataObject(signature);

		return signerInfo;

	}

	/**
	 * Return the SignerIdentifier to be used within the SignerInfo returned by
	 * {@link #getSignerInfo()}.
	 * <p/>
	 * The provided identifier is extracted from the certificate provided by
	 * {@link #getCertificate()}. If more than one certificate is used you need to
	 * override {@link #getSignerInfos()} anyhow and can safely ignore this method
	 *
	 * @return
	 */
	protected TlvDataObject getSid() {
		ConstructedTlvDataObject cert = getCertificate();

		TlvDataObject issuer = cert.getTlvDataObject(
				new TlvPath(new TlvTagIdentifier(TAG_SEQUENCE), new TlvTagIdentifier(TAG_SEQUENCE, 1)));

		TlvDataObject serialNumber = cert.getTlvDataObject(new TlvPath(TAG_SEQUENCE, TAG_INTEGER));

		return new ConstructedTlvDataObject(TAG_SEQUENCE, issuer, serialNumber);
	}

	/**
	 * Return the signed attributes to be used within the SignerInfo returned by
	 * {@link #getSignerInfo()}
	 *
	 * @param eContent
	 *            the encapContentInfo to be signed
	 * @return
	 */
	protected TlvDataObject getSignedAttrs(ConstructedTlvDataObject eContent) {
		ConstructedTlvDataObject signedAttrs = new ConstructedTlvDataObject(TAG_A0);

		// add contentType
		signedAttrs.addTlvDataObject(new ConstructedTlvDataObject(TAG_SEQUENCE,
				new PrimitiveTlvDataObject(TAG_OID, OID_contentType.toByteArray()), new ConstructedTlvDataObject(
						TAG_SET, new PrimitiveTlvDataObject(TAG_OID, encapContentInfoOid.toByteArray()))));

		// add messageDigest
		try {
			String digestAlg = "";
			// get digest algorithm name out of used digest algorithm id
			if (Arrays
					.equals(getDigestAlgorithm().toByteArray(),
							new ConstructedTlvDataObject(TAG_SEQUENCE,
									new PrimitiveTlvDataObject(TAG_OID, OID_SHA1.toByteArray()), noParameters)
											.toByteArray())) {
				digestAlg = "SHA1";
			} else if (Arrays.equals(getDigestAlgorithm().toByteArray(),
					new ConstructedTlvDataObject(TAG_SEQUENCE,
							new PrimitiveTlvDataObject(TAG_OID, OID_SHA224.toByteArray()), noParameters)
									.toByteArray())) {
				digestAlg = "SHA224";
			} else if (Arrays.equals(getDigestAlgorithm().toByteArray(),
					new ConstructedTlvDataObject(TAG_SEQUENCE,
							new PrimitiveTlvDataObject(TAG_OID, OID_SHA256.toByteArray()), noParameters)
									.toByteArray())) {
				digestAlg = "SHA256";
			} else if (Arrays.equals(getDigestAlgorithm().toByteArray(),
					new ConstructedTlvDataObject(TAG_SEQUENCE,
							new PrimitiveTlvDataObject(TAG_OID, OID_SHA384.toByteArray()), noParameters)
									.toByteArray())) {
				digestAlg = "SHA384";
			} else if (Arrays.equals(getDigestAlgorithm().toByteArray(),
					new ConstructedTlvDataObject(TAG_SEQUENCE,
							new PrimitiveTlvDataObject(TAG_OID, OID_SHA512.toByteArray()), noParameters)
									.toByteArray())) {
				digestAlg = "SHA512";
			}
			MessageDigest md = MessageDigest.getInstance(digestAlg);

			byte[] digest = md.digest(eContent.getTlvDataObject(new TlvPath(TAG_A0, TAG_OCTET_STRING)).getValueField());

			ConstructedTlvDataObject messageDigest = new ConstructedTlvDataObject(TAG_SEQUENCE);
			messageDigest.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, OID_messageDigest.toByteArray()));
			ConstructedTlvDataObject attrValues = new ConstructedTlvDataObject(TAG_SET,
					new PrimitiveTlvDataObject(TAG_OCTET_STRING, digest));
			messageDigest.addTlvDataObject(attrValues);

			signedAttrs.addTlvDataObject(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			logException(getClass(), e);
		}

		return signedAttrs;
	}

	/**
	 * Creates private key from an encoded EC private key
	 *
	 * @param encodedKey
	 *            private key as byte array
	 * @return the private key as PrivateKey object
	 * @throws InvalidKeySpecException
	 *             if the given key is not an EC key
	 */
	private PrivateKey getKeyFromByteArray(byte[] encodedKey) throws InvalidKeySpecException {
		KeyFactory keyFac;
		try {
			try {
				keyFac = KeyFactory.getInstance("RSA", Crypto.getCryptoProvider());
				PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(encodedKey);
				PrivateKey privK = keyFac.generatePrivate(pkcs8KeySpec);
				return privK;
			} catch (InvalidKeySpecException e) {
				keyFac = KeyFactory.getInstance("EC", Crypto.getCryptoProvider());
				PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(encodedKey);
				PrivateKey privK = keyFac.generatePrivate(pkcs8KeySpec);
				return privK;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Return the signature to be used within the SignerInfo returned by
	 * {@link #getSignerInfo()}
	 *
	 * @param sigInput
	 *            input to the signature generation process
	 * @return
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	protected byte[] getSignature(byte[] sigInput)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		String signatureAlg = getSignatureAlgorithmString();

		Signature signer = Signature.getInstance(signatureAlg, Crypto.getCryptoProvider());
		signer.initSign(dsPrivKey);
		signer.update(sigInput);
		return signer.sign();
	}
}
