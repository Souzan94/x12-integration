
package com.helger.as2.util;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;

import javax.annotation.Nonnull;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.regex.RegExHelper;

/**
 * 
 */
public final class ByteCoder
{
  private ByteCoder ()
  {}

  @Nonnull
  public static String encode (@Nonnull final String inStr)
  {
    final StringBuilder aSB = new StringBuilder (inStr.length () * 3);
    for (final byte element : inStr.getBytes (StandardCharsets.ISO_8859_1))
    {
      // Ensure unsigned int
      aSB.append ('.').append (element & 0xff).append ('.');
    }
    return aSB.toString ();
  }

  @Nonnull
  public static String decode (@Nonnull final String inStr)
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (inStr.length () / 3))
    {
      final Matcher aMatcher = RegExHelper.getMatcher (".[0-9]+.", inStr);
      while (aMatcher.find ())
      {
        final String sMatch = aMatcher.group ();
        // Ensure unsigned int
        final byte me = (byte) (Integer.parseInt (sMatch.substring (1, sMatch.length () - 1)) & 0xff);
        aBAOS.write (me);
      }
      return aBAOS.getAsString (StandardCharsets.ISO_8859_1);
    }
  }
}
