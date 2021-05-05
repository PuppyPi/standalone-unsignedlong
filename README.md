# standalone-unsignedlong
com.google.common.primitives.UnsignedLong from Google Guava, but with no dependencies, just a single .java file :)

This way, with a common Runtime Type, polymorphic code that accepts java.lang.Object's and uses instanceof to distinguish Strings from java.lang.Integer's from java.lang.Long's and etc. can now use UnsignedLong instead of a whole BigInteger for handling integers that come from unsigned 64-bit ints efficiently.

This is all you need for that, since signed long and int can handle the smaller unsigned 8-bit, 16-bit, etc. (and it takes hardly any more space, and quite likely actually no more space at all since sub-machine-word-length fields are often promoted to machine-word-length anyway for performance, so 'byte' or 'short' fields (as opposed to arrays) might easily each take 8 bytes under the hood with some just being wasted for performance)

All that to say, this is the only class missing from the JRE to enable efficient polymorphic handling of all common integers :)
