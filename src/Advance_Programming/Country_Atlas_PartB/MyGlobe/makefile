CC = gcc
COMP_FLAG = -c
OBJS = Countries.o LinkedList.o KeyValuePair.o HashTable.o Main.o
EXEC = MyGlobe

$(EXEC) : $(OBJS)
	$(CC) $(OBJS) -o $@

Countries.o : Countries.c Countries.h Defs.h
	$(CC) $(COMP_FLAG) $*.c

LinkedList.o : LinkedList.c LinkedList.h Defs.h
	$(CC) $(COMP_FLAG) $*.c

KeyValuePair.o : KeyValuePair.c KeyValuePair.h Defs.h
	$(CC) $(COMP_FLAG) $*.c

HashTable.o : HashTable.c HashTable.h Defs.h KeyValuePair.h LinkedList.h
	$(CC) $(COMP_FLAG) $*.c

Main.o : Main.c Defs.h Countries.h HashTable.h
	$(CC) $(COMP_FLAG) $*.c

clean :
	rm -f $(OBJS) $(EXEC)
