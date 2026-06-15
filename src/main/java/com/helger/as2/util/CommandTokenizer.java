
package com.helger.as2.util;

import javax.annotation.Nonnull;

import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.exception.WrappedOpenAS2Exception;

/**
 * emulates StringTokenizer
 *
 * 
 */
public class CommandTokenizer
{
  private final String m_sWorkString;
  private int m_nPos = 0;
  private final int m_nLen;

  /**
   * constructor
   *
   * @param inString
   *        in string
   */
  public CommandTokenizer (@Nonnull final String inString)
  {
    m_sWorkString = inString;
    m_nLen = m_sWorkString.length ();
  }

  /**
   * any more tokens in String
   *
   * @return true if there are any more tokens
   * @throws OpenAS2Exception
   *         in case another exception occurs
   */
  public boolean hasMoreTokens () throws OpenAS2Exception
  {
    try
    {
      while (m_nPos < m_nLen - 1 && m_sWorkString.charAt (m_nPos) == ' ')
        m_nPos++;

      if (m_nPos < m_nLen)
        return true;

      return false;
    }
    catch (final RuntimeException ex)
    {
      throw WrappedOpenAS2Exception.wrap (ex);
    }
  }

  /**
   * returns the next token, this handles spaces and quotes
   *
   * @return a string
   * @throws OpenAS2Exception
   *         In case a {@link RuntimeException} occurs
   */
  public String nextToken () throws OpenAS2Exception
  {
    try
    {
      while (m_nPos < m_nLen - 1 && m_sWorkString.charAt (m_nPos) == ' ')
        m_nPos++;

      final StringBuilder sb = new StringBuilder ();

      while (m_nPos < m_nLen && m_sWorkString.charAt (m_nPos) != ' ')
      {

        if (m_sWorkString.charAt (m_nPos) == '"')
        {
          m_nPos++;
          while (m_nPos < m_nLen && m_sWorkString.charAt (m_nPos) != '"')
          {
            sb.append (m_sWorkString.charAt (m_nPos));
            m_nPos++;
          }
          m_nPos++;
          return sb.toString ();
        }
        sb.append (m_sWorkString.charAt (m_nPos));
        m_nPos++;
      }

      return sb.toString ();
    }
    catch (final RuntimeException ex)
    {
      throw WrappedOpenAS2Exception.wrap (ex);
    }
  }
}
