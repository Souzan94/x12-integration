
package com.helger.as2.cmd;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.xml.microdom.IMicroContainer;
import com.helger.xml.microdom.MicroContainer;
import com.helger.xml.microdom.serialize.MicroWriter;

@NotThreadSafe
public final class CommandResult
{
  private final ECommandResultType m_eType;
  private final ICommonsList <Serializable> m_aResults = new CommonsArrayList <> ();

  public CommandResult (@Nonnull final ECommandResultType eType)
  {
    m_eType = ValueEnforcer.notNull (eType, "Type");
  }

  public CommandResult (@Nonnull final ECommandResultType eType, @Nonnull final String msg)
  {
    this (eType);
    addResult (msg);
  }

  public CommandResult (final Exception e)
  {
    super ();
    m_eType = ECommandResultType.TYPE_EXCEPTION;
    addResult (e);
  }

  @Nonnull
  public ECommandResultType getType ()
  {
    return m_eType;
  }

  public void addResult (@Nonnull final Serializable aResult)
  {
    ValueEnforcer.notNull (aResult, "Result");
    m_aResults.add (aResult);
  }

  public boolean hasNoResult ()
  {
    return m_aResults.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Serializable> getAllResults ()
  {
    return m_aResults.getClone ();
  }

  @Nonnull
  public String getResultAsString ()
  {
    final StringBuilder results = new StringBuilder ();
    for (final Serializable x : m_aResults)
      results.append (x.toString ()).append ("\r\n");
    return results.toString ();
  }

  @Nonnull
  public String getAsXMLString ()
  {
    final IMicroContainer aCont = new MicroContainer ();
    for (final Serializable x : m_aResults)
      aCont.appendElement ("result").appendText (x.toString ());
    return MicroWriter.getNodeAsString (aCont);
  }

  @Override
  public String toString ()
  {
    final StringBuilder buf = new StringBuilder ();
    buf.append (m_eType.getText ()).append (":\r\n");
    for (final Serializable aResult : m_aResults)
      buf.append (aResult.toString ()).append ("\r\n");
    return buf.toString ();
  }
}
