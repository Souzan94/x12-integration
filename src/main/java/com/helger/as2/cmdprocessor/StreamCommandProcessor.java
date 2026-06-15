
package com.helger.as2.cmdprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.as2.cmd.CommandResult;
import com.helger.as2.cmd.ICommand;
import com.helger.as2.util.CommandTokenizer;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.exception.WrappedOpenAS2Exception;
import com.helger.as2lib.session.IAS2Session;
import com.helger.commons.collection.attr.IStringMap;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.concurrent.ThreadHelper;
import com.helger.commons.string.StringHelper;

/**
 * original author unknown in this release made the process a thread so it can
 * be shared with other command processors like the SocketCommandProcessor
 * created innerclass CommandTokenizer so it could handle quotes and spaces
 * within quotes
 *
 * 
 */
public class StreamCommandProcessor extends AbstractCommandProcessor
{
  public static final String COMMAND_NOT_FOUND = "Error: command not found";
  public static final String COMMAND_ERROR = "Error executing command";
  public static final String EXIT_COMMAND = "exit";
  public static final String PROMPT = "AS2>";
  private final BufferedReader m_aReader;
  private final BufferedWriter m_aWriter;

  public StreamCommandProcessor ()
  {
    m_aReader = new BufferedReader (new InputStreamReader (System.in));
    m_aWriter = new BufferedWriter (new OutputStreamWriter (System.out));
  }

  public void initDynamicComponent (@Nonnull final IAS2Session session,
                                    @Nullable final IStringMap parameters) throws OpenAS2Exception
  {}

  @Nonnull
  public BufferedReader getReader ()
  {
    return m_aReader;
  }

  @Nonnull
  public BufferedWriter getWriter ()
  {
    return m_aWriter;
  }

  @Override
  public void run ()
  {
    try
    {
      while (true)
        processCommand ();
    }
    catch (final OpenAS2Exception e)
    {
      e.printStackTrace ();
    }
  }

  @Override
  public void processCommand () throws OpenAS2Exception
  {
    try
    {
      final String sLine = readLine ();
      if (sLine != null)
      {
        final CommandTokenizer aTokenizer = new CommandTokenizer (sLine);
        if (aTokenizer.hasMoreTokens ())
        {
          final String sCommandName = aTokenizer.nextToken ().toLowerCase (Locale.US);

          if (sCommandName.equals (EXIT_COMMAND))
          {
            terminate ();
          }
          else
          {
            final ICommonsList <String> aParams = new CommonsArrayList <> ();
            while (aTokenizer.hasMoreTokens ())
            {
              aParams.add (aTokenizer.nextToken ());
            }

            final ICommand aCommand = getCommand (sCommandName);
            if (aCommand != null)
            {
              final CommandResult aResult = aCommand.execute (aParams.toArray ());
              if (aResult.getType ().isSuccess ())
              {
                writeLine (aResult.toString ());
              }
              else
              {
                writeLine (COMMAND_ERROR);
                writeLine (aResult.getResultAsString ());
              }
            }
            else
            {
              writeLine (COMMAND_NOT_FOUND + "> " + sCommandName);
              writeLine ("List of commands:");
              writeLine (EXIT_COMMAND);
              for (final String sCurCmd : getAllCommands ().keySet ())
                writeLine (sCurCmd);
            }
          }
        }

        write (PROMPT);
      }
      else
      {
        ThreadHelper.sleep (100);
      }
    }
    catch (final IOException ex)
    {
      throw WrappedOpenAS2Exception.wrap (ex);
    }
  }

  @Nullable
  public String readLine () throws IOException
  {
    final BufferedReader aReader = getReader ();

    return StringHelper.trim (aReader.readLine ());
  }

  public void write (final String text) throws IOException
  {
    final BufferedWriter aWriter = getWriter ();
    aWriter.write (text);
    aWriter.flush ();
  }

  public void writeLine (final String line) throws IOException
  {
    final BufferedWriter aWriter = getWriter ();
    aWriter.write (line + "\r\n");
    aWriter.flush ();
  }
}
