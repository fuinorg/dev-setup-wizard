/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.devsupwiz.tasks.gitsetup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.validation.constraints.NotEmpty;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

/**
 * Generates a new key pair for SSH.
 */
public final class SshKeyPairGenerator {

    private final String user;
    
    private final KeyPair keyPair;
    
    private final String privateKey;
    
    private final String publicKey;
    
    /**
     * Constructor with user name.
     * 
     * @param user Username.
     */
    public SshKeyPairGenerator(@NotEmpty final String user) {
        super();
        
        this.user = user;        
        try {
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA",
                    BouncyCastleProvider.PROVIDER_NAME);
            kpg.initialize(2048);
            keyPair = kpg.generateKeyPair();
            privateKey = createPrivate(keyPair);
            publicKey = createPublic(keyPair, user);            
        } catch (final NoSuchAlgorithmException | NoSuchProviderException ex) {
            throw new RuntimeException("Failed to generate a key pair for SSH", ex);
        }
        
    }

    /**
     * Returns the user.
     * 
     * @return User name.
     */
    public final String getUser() {
        return user;
    }

    /**
     * Returns the key pair.
     * 
     * @return Newly generated key pair.
     */
    public final KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Returns the private key string.
     * 
     * @return Private key as stored in "~/.ssh/id_rsa".
     */
    public final String getPrivateKey() {
        return privateKey;
    }

    /**
     * Returns the public key string.
     * 
     * @return Public key as stored in "~/.ssh/id_rsa.pub".
     */
    public final String getPublicKey() {
        return publicKey;
    }

    /**
     * Encodes the public key according to RFC #4253.
     * 
     * @param key
     *            Public key to encode.
     * 
     * @return Base64 encoded key.
     * 
     * @throws IOException
     *             Error writing the key.
     */
    private static String encodePublicKey(final RSAPublicKey key)
            throws IOException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        /* encode the "ssh-rsa" string */
        final byte[] sshrsa = new byte[] { 0, 0, 0, 7, 's', 's', 'h', '-', 'r',
                's', 'a' };
        out.write(sshrsa);

        /* Encode the public exponent */
        final BigInteger e = key.getPublicExponent();
        final byte[] exponent = e.toByteArray();
        out.write(encodeUInt32(exponent.length));
        out.write(exponent);

        /* Encode the modulus */
        final BigInteger m = key.getModulus();
        final byte[] modulus = m.toByteArray();
        out.write(encodeUInt32(modulus.length));
        out.write(modulus);

        final byte[] bytes = out.toByteArray();
        return new String(Base64.getEncoder().encode(bytes),
                Charset.forName("us-ascii"));
    }

    /**
     * Converts the integer value into an unsigned 32 integer.
     * 
     * @param value
     *            Value to convert.
     * 
     * @return Integer as byte array.
     */
    private static byte[] encodeUInt32(final int value) {
        final byte[] tmp = new byte[4];
        tmp[0] = (byte) ((value >>> 24) & 0xff);
        tmp[1] = (byte) ((value >>> 16) & 0xff);
        tmp[2] = (byte) ((value >>> 8) & 0xff);
        tmp[3] = (byte) (value & 0xff);
        return tmp;
    }

    /**
     * Creates the public key string.
     * 
     * @param keyPair
     *            Key pair.
     * @param user
     *            User the key is for.
     * 
     * @return String to be stored in a public key file.
     */
    private static String createPublic(final KeyPair keyPair,
            final String user) {
        try {
            final RSAPublicKey key = (RSAPublicKey) keyPair.getPublic();
            return "ssh-rsa " + encodePublicKey(key) + " " + user;
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates the text for the private key file.
     * 
     * @param keyPair
     *            Key pair with the private key.
     * 
     * @return String to be stored in the public key file.
     */
    private static String createPrivate(final KeyPair keyPair) {
        try {
            final StringWriter strWriter = new StringWriter();
            try (final JcaPEMWriter pemWriter = new JcaPEMWriter(strWriter)) {
                pemWriter.writeObject(keyPair.getPrivate());
            }
            return strWriter.toString();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
