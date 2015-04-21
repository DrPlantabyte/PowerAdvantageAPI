/**
This package contains the algorithm used to connect the energy producers to the energy consumers. 
It does this by employing a search algorithm to follow all adjacent ITypedConduit blocks that share 
the same power type and caching the resulting list of sources and sinks. Every time a conduit block 
is placed or broken, any adjacent networks are revalidated.
*/
package cyano.poweradvantage.conduitnetwork;