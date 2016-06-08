library(dbscan)
#import the dataset from the environment
res <-optics(`5_dataset`,10,15)
result3<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.3)
plot(res$reachdist[res$order], type="h", col=result3[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#result three contains only one cluster and this is correct looking at the plot. Lower then -0.3 gives the same result
result<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.36)
plot(res$reachdist[res$order], type="h", col=result[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#observation: there is one cluster, while it should be three clusters, also not everything is included in the cluster and there are some noise(black) and (red ) lines in between

result2<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.4)
#In the second observation we can find that there are three clusters. One at the side which looks complete wrong(dark blue), one green one which contains the bigger one and two low blue clusters
#[[1]]
# [1]   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21  22  23  24  25  26  27  28  29
# [30]  30  31  32  33  34  35  36  37  38  39  40  41  42  43  44  45  46  47  48  49  50  51  52  53  54  55  56  57  58
# [59]  59  60  61  62  63  64  65  66  67  68  69  70  71  72  73  74  75  76  77  78  79  80  81  82  83  84  85  86  87
# [88]  88  89  90  91  92  93  94  95  96  97  98  99 100 101 102 103 104 105 106 107 108 109 110 111 112 113 114 115 116
# [117] 117 118 119 120 121 122 123 124 125 126 127 128 129 130 131 132 133 134 135 136 137 138 139 140 141 142 143 144 145
# [146] 146 147 148 149 150 151 152 153 154 155 156 157 158 159 160 161 162 163 164 165 166 167 168 169 170 171 172 173 174
# [175] 175 176 177 178 179 180 181 182 183 184 185 186 187 188 189 190 191 192 193 194 195 196 197 198 199 200 201 202 203
# [204] 204 205 206 207 208 209 210 211 212 213 214 215 216 217 218 219 220 221 222 223 224 225 226 227 228 229 230 231 232
# [233] 233 234 235 236 237 238
# 
# [[2]]
# [1]   1   2   3   4   5   6  38  39  40  41  42  43  44  45  46  47  48  49  50  51  52  53  54  55  56 146 154 155 161
# [30] 167 169 171 176 180 184 191 195 203 207 210 212 217 218 219 222 225 230 237
# 
# [[3]]
# [1]  1  2  3  4  5  6 48 49 50 51 52 53 54 55 56
# 
# [[4]]
# [1]  1  2  3  4  5  6 50 51 52 53 54 55 56

plot(res$reachdist[res$order], type="h", col=result3[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")

result4<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.5)
plot(res$reachdist[res$order], type="h", col=result4[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#Here something strange happens there is still clusters as in the result before, but now the bigger up area in the middle is seperated in two clusters
result5<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.6)
plot(res$reachdist[res$order], type="h", col=result5[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#Here almost everything is seen as noise which is strange, because we are expecting more clustrers in the up area however now more clusters are found. 
result6<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-07)
plot(res$reachdist[res$order], type="h", col=result6[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#The observation here is that there are many small clusters, this where compared to the next and the one before there are not many small clusters, this seems that the angle is super influencing
result7<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.8)
plot(res$reachdist[res$order], type="h", col=result7[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#This gives a quite similar result to 8 howeer now the outer ring has bigger clusters 
result8<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.9)
plot(res$reachdist[res$order], type="h", col=result8[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#Observation: Here the two big clusters are found, however there are small clusters within the two big clusters ther reason for this is not quite clear. Also the steep hills wich is the out ring has different clusters in it. The possile reason is the small angle changes, however this is not clear.
result9<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-1)
plot(res$reachdist[res$order], type="h", col=result9[res$order]+1L, ylab ="Reachability dist.", xlab ="OPTICS order", main = "Reachability Plot")
#The observation here is that there are many small clusters here and all clusters are shown, this result is to be expected