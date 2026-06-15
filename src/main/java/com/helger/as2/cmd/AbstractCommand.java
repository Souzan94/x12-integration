
package com.helger.as2.cmd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.as2lib.AbstractDynamicComponent;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.session.IAS2Session;
import com.helger.commons.collection.attr.IStringMap;

public abstract class AbstractCommand extends AbstractDynamicComponent implements ICommand
{
  public static final String ATTR_NAME = "name";
  public static final String ATTR_DESCRIPTION = "description";
  public static final String ATTR_USAGE = "usage";

  @Override
  public void initDynamicComponent (@Nonnull final IAS2Session session,
                                    @Nullable final IStringMap parameters) throws OpenAS2Exception
  {
    super.initDynamicComponent (session, parameters);
    if (getName () == null)
      setName (getDefaultName ());
    if (getDescription () == null)
      setDescription (getDefaultDescription ());
    if (getUsage () == null)
      setUsage (getDefaultUsage ());
  }

  @Nullable
  public String getDescription ()
  {
    return attrs ().getAsString (ATTR_DESCRIPTION);
  }

  public void setDescription (final String desc)
  {
    attrs ().putIn (ATTR_DESCRIPTION, desc);
  }

  @Override
  @Nullable
  public String getName ()
  {
    return attrs ().getAsString (ATTR_NAME);
  }

  public void setName (final String name)
  {
    attrs ().putIn (ATTR_NAME, name);
  }

  @Nullable
  public String getUsage ()
  {
    return attrs ().getAsString (ATTR_USAGE);
  }

  public void setUsage (final String usage)
  {
    attrs ().putIn (ATTR_USAGE, usage);
  }

  public abstract String getDefaultName ();

  public abstract String getDefaultDescription ();

  public abstract String getDefaultUsage ();
}
