import React from "react";
import {
    PieChart,
    Pie,
    Cell,
    Tooltip,
    Legend,
    BarChart,
    Bar,
    XAxis,
    YAxis,
} from "recharts";
import { PaymentSalesStats } from "../../types/dashboard";
import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
} from "@/components/ui/tabs";

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#A569BD"];

interface Props {
    performanceData: PaymentSalesStats[];
    genreData: PaymentSalesStats[];
}

const SalesByCategorySection: React.FC<Props> = ({ performanceData, genreData }) => {
    return (
        <div className="p-4 bg-white rounded-2xl shadow">
            <h2 className="text-lg font-bold mb-4">매출 통계</h2>

            <Tabs defaultValue="performance">
                <TabsList className="mb-4">
                    <TabsTrigger value="performance">공연별</TabsTrigger>
                    <TabsTrigger value="genre">장르별</TabsTrigger>
                </TabsList>

                <TabsContent value="performance">
                    <PieChart width={500} height={400}>
                        <Pie
                            data={performanceData}
                            dataKey="totalAmount"
                            nameKey="label"
                            cx="50%"
                            cy="50%"
                            outerRadius={150}
                            label
                        >
                            {performanceData.map((_, index) => (
                                <Cell
                                    key={index}
                                    fill={COLORS[index % COLORS.length]}
                                />
                            ))}
                        </Pie>
                        <Tooltip formatter={(value) => `${Number(value).toLocaleString()}원`} />
                        <Legend />
                    </PieChart>
                </TabsContent>

                <TabsContent value="genre">
                    <BarChart width={600} height={400} data={genreData}>
                        <XAxis dataKey="label" />
                        <YAxis />
                        <Tooltip formatter={(value) => `${Number(value).toLocaleString()}원`} />
                        <Legend />
                        <Bar dataKey="totalAmount" fill="#82ca9d" />
                    </BarChart>
                </TabsContent>
            </Tabs>
        </div>
    );
};

export default SalesByCategorySection;
