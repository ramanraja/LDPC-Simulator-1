Generates a sequence of LDPC code pairs by Gallager or PEG algorithm.
Parameters of the sequence are read from a text file.
 * For each code pair H1 and H2 in the sequence :
Runs a decoder simulation with H1 for different erasure probabilities. 
Repeats many times.
If the decoder fails, takes the stopping set and finds the column rank of the columns of H2, 
corresponding to the stopping set.
Calculates cumulative and average statistics over several trials for each probability.
Records the statistics in a CSV file that can be opened in XL.

Individual stats:
----------------
RunID
Duration
separate for H1 and H2:
CodeID1/ CodeID2
Algorithm (Gallager/Modified-Gallager/PEG)
Rows
Columns 
Edges
Left distribution (comma separated/ polynomial)
Right distribution (comma separated/ polynomial)
 

Error Probability
Error Count
Decoder Result (success/ failure)
Stopping set size
Column rank of H2
Stopping set in H1 (comma separated)

Cumulative stats :
----------------
CodeID1 
CodeID2
Error Probability
Number of trials
Success count
Failure Count
Success ratio
Average stopping set size
Average column rank

Totals :
--------
Threshold probability (inflection of success ratio)
Average stopping set size before threshold
Average column rank before threshold
Average stopping set size after threshold
Average column rank after threshold


