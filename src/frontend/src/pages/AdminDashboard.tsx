import DashboardContent from "../components/DashboardContent";
import DashboardCards from "../components/DashboardCards";
import TotalSummaryCards from "../components/TotalSummaryCards";

export default function AdminDashboard() {
    return (
        <div className="space-y-6">
            <TotalSummaryCards />
            <DashboardCards />
            <DashboardContent />
        </div>
    );
}
