
/// /// /// /// /// /// /// /// THE PROBLEM /// /// /// /// /// /// /// /// /// 

Each character on a computer is assigned a unique code and the preferred 
standard is ASCII (American Standard Code for Information Interchange). For 
example, uppercase A = 65, asterisk (*) = 42, and lowercase k = 107.

A modern encryption method is to take a text file, convert the bytes to ASCII, 
then XOR each byte with a given value, taken from a secret key. The advantage 
with the XOR function is that using the same encryption key on the cipher text, 
restores the plain text; for example, 65 XOR 42 = 107, then 107 XOR 42 = 65.

For unbreakable encryption, the key is the same length as the plain text 
message, and the key is made up of random bytes. The user would keep the 
encrypted message and the encryption key in different locations, and without 
both "halves", it is impossible to decrypt the message.

Unfortunately, this method is impractical for most users, so the modified method
is to use a password as a key. If the password is shorter than the message, 
which is likely, the key is repeated cyclically throughout the message. The 
balance for this method is using a sufficiently long password key for security, 
but short enough to be memorable.

/// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// ///


This is my solution to breaking this encryption method. 

To find the keys I origionally started looking for the most common orrurances of
the top 10 letters in the english language and created a "score" that each key 
was compared to. The highest score for each length was saved and then the best 
score of those was selected as the best key. The problem with this approach was 
that it was possible for a key to produce a garbled output of nonsense that just
happened to have a greated occurance of those 10 english letters. The other 
problem was that I had to  search for keys based on guessing a probable max key 
length and testing all keys shorter than it. This proved to be inefficent 
because if you didn't guess high enough then you wouldn't find the key and if 
you guessed too low then you would have to wait for the rest of the keys to 
finish processing even after you found the right one. It was either too slow or 
didn't work at all. 

To fix this I incorporated a three new tests for checking proper grammar in the 
English language. The keys are now tested by taking the encrypted file and 
producing tokens for every time a space character is found. These tokens go 
throw a "QU" test (|Q| must be followed by |U|), a Triple Letter test (No 
english word has the same letter three times in a row), and a punctuation test 
(!?,.&). This fixes the problem of recieving a random mess for output and at the
same time allows testing of keys to start at length 1 and work up to exactly the
correct key length and not any more or less.

I've now also incorporated multi-threading so that runtime is significantly 
reduced. The user specifies the number of threads to run as args 0. these 
threads then take a length starting at 1 and incrementing by 1 to test for keys.
Once a thread deems a length incorrect it then takes the next higher length out 
of the collective ones being tested. Once a thread finds the correct key based 
off the tests it then prints it to screen and terminated the other threads 
wherever they are to stop the process all at once.
