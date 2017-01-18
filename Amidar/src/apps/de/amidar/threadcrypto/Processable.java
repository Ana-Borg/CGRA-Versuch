package de.amidar.threadcrypto;

public interface Processable {
	public void process();
	public byte[] getEncrypted();
	public void setEncrypted(byte[] e);
}
