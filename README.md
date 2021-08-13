# Faster Rebrickable

## Problem Statement: 
- Which lego sets and what percentage a user can compose with the existing sets at their home by reusing the parts?
- Website rebrickable does it but it is too slow. 
- Around 10-15 seconds for a query 
- Only a single request at a time

## Solution
- Use Hazelcast platform compute and persistence capabilities and decrease the time down to msecs(750 msecs).

## Matching algorithm:
- User enters the 3 lego sets that he/she has at home
- We find the Lego parts of the user’s old sets and combine them.
- Run a test against each of 10713 sets and find what percentage of the set can be composed with the existing parts.
- Need many comparisons: (10713 * average_number_of_parts_per_set * old_parts_number)
- Sequential algorithm takes minutes.
- Rebrickable does it around 10-15 seconds or more.

## Our Implementation
- Use Hazelcast compute power for the compute intensive algorithm:
- Parse the csv files and load the Lego data set into the Hazelcast Imap. Use persistence so that this will not be repeated again but server will have the data ready on startup.
- Use Lego set identifier as the key and submit jet jobs which will do the computation in parallel. Locality using jet mapping makes the computation local to the member node.
- Aggregate the results in a result imap.
- Use SQL select statement to display the first 50 items with matching percentage of highest first.
- Added map index for percentage to order the Imap in terms of the percentage.

## Results
- Time drops down to 750 msecs for a query!
- Can run multiple queries at the same time.
- When the result can be obtained this fast, the user can be presented the results immediately as he/she types the existing Lego Set ids at the web site, no need to click on submit. Better user experience.
