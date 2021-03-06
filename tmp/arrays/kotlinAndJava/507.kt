//File A.java
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

public interface A {
   @NotNull
   String foo();
}


//File AImpl.java
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class AImpl implements A {
   @NotNull
   private final String z;

   @NotNull
   public String foo() {
      return this.z;
   }

   @NotNull
   public final String getZ() {
      return this.z;
   }

   public AImpl(@NotNull String z) {
      super();
      this.z = z;
   }
}


//File AFabric.java
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public class AFabric {
   @NotNull
   public A createA(@NotNull String z) {
      return (A)(new AImpl(z));
   }
}


//File AWrapperFabric.java
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class AWrapperFabric extends AFabric {
   @NotNull
   public A createA(@NotNull String z) {
      return (A)(new AImpl("fail: " + z));
   }

   @NotNull
   public final A createMyA() {
      final String z = "OK";
      return (A)(new A() {
         // $FF: synthetic field
         private final A $$delegate_0 = AWrapperFabric.super.createA(z);

         @NotNull
         public String foo() {
            return this.$$delegate_0.foo();
         }
      });
   }
}


//File Main.kt


fun box(): String {
    return AWrapperFabric().createMyA().foo()
}

