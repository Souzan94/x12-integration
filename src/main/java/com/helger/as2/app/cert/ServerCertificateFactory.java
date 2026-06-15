
package com.helger.as2.app.cert;

import java.io.File;
import java.io.InputStream;

import javax.annotation.WillClose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.as2.util.EFileMonitorEvent;
import com.helger.as2.util.FileMonitor;
import com.helger.as2.util.IFileMonitorListener;
import com.helger.as2lib.cert.CertificateFactory;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.params.InvalidParameterException;

public class ServerCertificateFactory extends CertificateFactory implements IFileMonitorListener
{
  public static final String ATTR_INTERVAL = "interval";
  private static final Logger LOGGER = LoggerFactory.getLogger (ServerCertificateFactory.class);

  private FileMonitor m_aFileMonitor;

  @Override
  public void load (@WillClose final InputStream in, final char [] password) throws OpenAS2Exception
  {
    super.load (in, password);
    getFileMonitor ();
  }

  public void setFileMonitor (final FileMonitor fileMonitor)
  {
    m_aFileMonitor = fileMonitor;
  }

  public FileMonitor getFileMonitor () throws InvalidParameterException
  {
    boolean bCreateMonitor = m_aFileMonitor == null && attrs ().getAsString (ATTR_INTERVAL) != null;
    if (!bCreateMonitor && m_aFileMonitor != null)
    {
      final String filename = m_aFileMonitor.getFilename ();
      bCreateMonitor = filename != null && !filename.equals (getFilename ());
    }

    if (bCreateMonitor)
    {
      if (m_aFileMonitor != null)
        m_aFileMonitor.stop ();

      final int nInterval = getAttributeAsIntRequired (ATTR_INTERVAL);
      final File file = new File (getFilename ());
      m_aFileMonitor = new FileMonitor (file, nInterval);
      m_aFileMonitor.addListener (this);
    }

    return m_aFileMonitor;
  }

  public void onFileMonitorEvent (final FileMonitor monitor, final File file, final EFileMonitorEvent eEvent)
  {
    switch (eEvent)
    {
      case EVENT_MODIFIED:
        try
        {
          load ();
          if (LOGGER.isInfoEnabled ())
            LOGGER.info ("- Certificates Reloaded -");
        }
        catch (final OpenAS2Exception oae)
        {
          oae.terminate ();
        }
        break;
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    // No field added
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    // No field added
    return super.hashCode ();
  }
}
