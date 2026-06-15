
package com.helger.as2.app.partner;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.as2.util.EFileMonitorEvent;
import com.helger.as2.util.FileMonitor;
import com.helger.as2.util.IFileMonitorListener;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.exception.WrappedOpenAS2Exception;
import com.helger.as2lib.params.InvalidParameterException;
import com.helger.as2lib.partner.xml.SelfFillingXMLPartnershipFactory;
import com.helger.commons.string.StringHelper;

/**
 * Same as {@link ServerXMLPartnershipFactory} but based on the
 * {@link SelfFillingXMLPartnershipFactory}
 *
 * 
 * 
 */
public class ServerSelfFillingXMLPartnershipFactory extends SelfFillingXMLPartnershipFactory implements
                                                    IFileMonitorListener
{
  public static final String ATTR_INTERVAL = "interval";
  private static final Logger LOGGER = LoggerFactory.getLogger (ServerSelfFillingXMLPartnershipFactory.class);

  private FileMonitor m_aFileMonitor;

  public void setFileMonitor (@Nullable final FileMonitor aFileMonitor)
  {
    m_aFileMonitor = aFileMonitor;
  }

  @Nonnull
  public FileMonitor getFileMonitor () throws InvalidParameterException
  {
    boolean bCreateMonitor = m_aFileMonitor == null && attrs ().containsKey (ATTR_INTERVAL);

    if (!bCreateMonitor && m_aFileMonitor != null)
    {
      final String sFilename = m_aFileMonitor.getFilename ();
      bCreateMonitor = StringHelper.hasText (sFilename) && !sFilename.equals (getFilename ());
    }

    if (bCreateMonitor)
    {
      if (m_aFileMonitor != null)
        m_aFileMonitor.stop ();

      final int nInterval = getAttributeAsIntRequired (ATTR_INTERVAL);
      final File aFile = new File (getFilename ());
      m_aFileMonitor = new FileMonitor (aFile, nInterval);
      m_aFileMonitor.addListener (this);
    }

    return m_aFileMonitor;
  }

  public void onFileMonitorEvent (final FileMonitor monitor, final File file, @Nonnull final EFileMonitorEvent eEvent)
  {
    switch (eEvent)
    {
      case EVENT_MODIFIED:
        try
        {
          refreshPartnershipFactory ();
          if (LOGGER.isInfoEnabled ())
            LOGGER.info ("- Partnerships Reloaded -");
        }
        catch (final OpenAS2Exception oae)
        {
          oae.terminate ();
        }

        break;
    }
  }

  @Override
  public void refreshPartnershipFactory () throws OpenAS2Exception
  {
    super.refreshPartnershipFactory ();
    try
    {
      getFileMonitor ();
    }
    catch (final Exception ex)
    {
      throw WrappedOpenAS2Exception.wrap (ex);
    }
  }
}
