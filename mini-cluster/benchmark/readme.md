hadoop jar hadoop-*-examples*.jar teragen -Dmapred.map.tasks=100 10000000 terasort/1T-input


hadoop jar hadoop-*-examples*.jar terasort -Dmapred.reduce.tasks=50 \
/user/root/terasort/1T-input /user/root/terasort/1T-output