package myagent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class MyTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("mylib/Adder")) {
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.makeClass(
                    new ByteArrayInputStream(classfileBuffer)
                );
                CtMethod m = cc.getDeclaredMethod("doInt");
                m.insertBefore("{ System.out.println(\"=> Before adding int\"); }");
                classfileBuffer = cc.toBytecode();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }

        if (className.equals("theapp/HelloWorld")) {
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.makeClass(
                    new ByteArrayInputStream(classfileBuffer)
                );
                CtMethod m = cc.getDeclaredMethod("main");
                m.setBody("{ System.out.println(\"Hello from MyAgent!\");"+
                    "System.out.println(\"Sum of 80 + 75 = !\" + mylib.Adder.doInt(80,75)); }");
                m.insertBefore("{ System.out.println(\"=> Before main\"); }");
                classfileBuffer = cc.toBytecode();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}
