# cryptanalysis-tool

A tool used as part of my 3rd year computer science degree for my dissertation.
This tool filters plain-text through a substiution-permutation network to output (plain-text, intermediate-text, cipher-text) triples in a csv file. It uses randomly generated keys for this to receive a spread of different results.
After this, an evolutionary algorithm is applied to the network to attempt to find weaknesses within the given SPN.
