// @file     Md5.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

import java.security.MessageDigest

public class Md5 {

  public static String encode(String message) {
    def digest = MessageDigest.getInstance("MD5")
    digest.update(message.bytes)
    def big = new BigInteger(1,digest.digest())
    return big.toString(16).padLeft(32,"0")
  }

}

// *EOF*


