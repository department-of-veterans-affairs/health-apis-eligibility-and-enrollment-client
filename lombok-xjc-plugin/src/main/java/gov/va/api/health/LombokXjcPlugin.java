package gov.va.api.health;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.xml.sax.ErrorHandler;

public final class LombokXjcPlugin extends Plugin {
  private static void annotateClass(final ClassOutline classOutline) {
    final JDefinedClass clazz = classOutline.implClass;
    if (!clazz.isAbstract()) {
      clazz.annotate(Builder.class);
    }
    final JAnnotationUse toString = clazz.annotate(ToString.class);
    if (clazz._extends() instanceof JDefinedClass) {
      toString.param("callSuper", true);
    }
    final JAnnotationUse equalsAndHashCode = clazz.annotate(EqualsAndHashCode.class);
    if (clazz._extends() instanceof JDefinedClass) {
      equalsAndHashCode.param("callSuper", true);
    }
    clazz.annotate(NoArgsConstructor.class);
    if (!clazz.fields().isEmpty()) {
      clazz.annotate(AllArgsConstructor.class).param("access", AccessLevel.PRIVATE);
    }
  }

  @Override
  public String getOptionName() {
    return "Xlombok";
  }

  @Override
  public String getUsage() {
    return "  -Xlombok    :  add Lombok annotations to generated classes";
  }

  @Override
  public int parseArgument(Options opt, String[] args, int i) {
    return 0;
  }

  @Override
  public boolean run(
      final Outline outline, final Options options, final ErrorHandler errorHandler) {
    for (final ClassOutline classOutline : outline.getClasses()) {
      annotateClass(classOutline);
    }
    return true;
  }
}
